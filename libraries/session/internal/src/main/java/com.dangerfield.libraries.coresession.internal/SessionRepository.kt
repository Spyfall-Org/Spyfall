package com.dangerfield.libraries.coresession.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.childSupervisorScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.storage.datastore.distinctKeyFlow
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.User
import com.dangerfield.libraries.session.UserRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import oddoneout.core.ApplicationStateRepository
import oddoneout.core.ForegroundState
import oddoneout.core.GenerateLocalUUID
import oddoneout.core.Try
import oddoneout.core.getOrElse
import oddoneout.core.logOnError
import oddoneout.core.readJson
import oddoneout.core.throwIfDebug
import oddoneout.core.toJson
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Singleton
class SessionRepository @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val datastore: DataStore<Preferences>,
    private val moshi: Moshi,
    applicationStateRepository: ApplicationStateRepository,
) {

    val session = LazySession()
    val sessionFlow: Flow<Session> get() = sharedSessionFlow

    // TODO investigate when this doesnt load, have some fallback, have some tracking
    private val sessionIdFlow: Flow<Long> = applicationStateRepository
        .foregroundStateFlow()
        .map {
            val sessionId = tryWithTimeout(5.seconds) {
                Try {
                    firebaseAnalytics.sessionId.await()!!
                }
            }
                .logOnError()
                .getOrElse {
                    Timber.d("SessionRepository: session id failed. generating local session id")
                    DEFAULT_SESSION_ID
                }

           sessionId
        }
        .filterNotNull()

    private val activeGameFlow: Flow<ActiveGame?> = datastore.distinctKeyFlow(ACTIVE_GAME_KEY)
            .map {
                it?.let {
                    moshi.readJson<ActiveGame>(it)
                }
            }

    private val sharedSessionFlow: SharedFlow<Session> = combine(
        activeGameFlow,
        sessionIdFlow,
        userRepository.getUserFlow(),
    ) { activeGame, sessionId, user ->

        val cachedSessionData = getCachedSessionData(sessionId)

        val sessionData = if (cachedSessionData == null) {
            clearSessionData()
            createSessionDataCache(sessionId)
        } else {
            cachedSessionData
        }

        object : Session {
            override val user = user
            override val startedAt = sessionData.startedAt
            override val activeGame = activeGame
            override val sessionId = sessionId
        }
    }.distinctUntilChanged()
        .shareIn(
            applicationScope,
            replay = 1,
            started = SharingStarted.Eagerly
        )

    suspend fun updateActiveGame(activeGame: ActiveGame?) {
        applicationScope.childSupervisorScope(dispatcherProvider.io).launch {
            datastore.edit { prefs ->
                prefs[ACTIVE_GAME_KEY] = activeGame?.let { game ->
                    moshi.toJson(game)
                } ?: ""
            }
        }
    }

    private suspend fun clearSessionData() {
        datastore.edit { it[SESSION_DATA_KEY] = "" }
    }

    private suspend fun getCachedSessionData(id: Long): SessionData? =
        datastore.data.first()[SESSION_DATA_KEY]?.let {
            val sessionData = Try { moshi.readJson<SessionData>(it) }.throwIfDebug().getOrNull()
            if (sessionData?.id == id) sessionData else null
        }

    private suspend fun createSessionDataCache(id: Long): SessionData {
        val sessionData = SessionData(
            id = id,
            startedAt = System.currentTimeMillis()
        )
        datastore.edit { it[SESSION_DATA_KEY] = moshi.toJson(sessionData) }
        return sessionData
    }

    companion object {
        private val ACTIVE_GAME_KEY = stringPreferencesKey("active_game")
        private val SESSION_DATA_KEY = stringPreferencesKey("session_data")
        private const val DEFAULT_SESSION_ID = 12345678910L

        /**
         * The maximum amount of time the application can be in the background before the
         * session is considered ended
         */
        val SESSION_MAXIMUM_TIME_AWAY = 10.minutes
    }

    /**
     * Implementation that will lazily load the [Session]'s properties.
     *
     * WARNING: If a value does not exist when invoked it will be fetched in a BLOCKING manner
     *
     * This allows [Session] to be injected anywhere without the need to have the data loaded.
     *
     * Session is loaded on app launch and will be available immediately after.
     * WARNING: Session Values should not be accessed during app launch to avoid blocking
     */
    inner class LazySession : Session {

        override val user: User
            get() = (sharedSessionFlow.replayCache.firstOrNull()
                ?: runBlocking { sessionFlow.first() })
                .user

        override val startedAt: Long?
            get() = (sharedSessionFlow.replayCache.firstOrNull()
                ?: runBlocking { sessionFlow.first() })
                .startedAt

        override val activeGame: ActiveGame?
            get() = (sharedSessionFlow.replayCache.firstOrNull()
                ?: runBlocking { sessionFlow.first() })
                .activeGame

        override val sessionId: Long?
            get() = (sharedSessionFlow.replayCache.firstOrNull()
                ?: runBlocking { sessionFlow.first() })
                .sessionId
    }
}