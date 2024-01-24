package com.dangerfield.libraries.coresession.internal.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.dangerfield.libraries.storage.datastore.distinctKeyFlow
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.session.GameKey
import com.dangerfield.libraries.session.Stats
import com.dangerfield.libraries.session.ThemeConfig
import com.dangerfield.libraries.session.User
import com.dangerfield.libraries.session.UserRepository
import com.dangerfield.libraries.session.storage.MeGamePlayed
import com.dangerfield.libraries.session.storage.MeGameResult
import com.dangerfield.libraries.session.storage.MeGameStatsDao
import com.dangerfield.libraries.ui.color.ThemeColor
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.Try
import oddoneout.core.logOnError
import oddoneout.core.withBackoffRetry
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@AutoBind
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseAnalytics: FirebaseAnalytics,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val meGameStatsDao: MeGameStatsDao,
    private val dispatcherProvider: DispatcherProvider,
) : UserRepository {

    private val colorConfigFlow = dataStore.distinctKeyFlow(ColorConfigKey)
        .map { cachedValue ->
            cachedValue?.let { string ->
                if (string == RandomColorConfigValue) {
                    ColorConfig.Random
                } else {
                    Try { ThemeColor.valueOf(string) }
                        .getOrNull()
                        ?.let { ColorConfig.Specific(it) }
                        ?: ColorConfig.Random
                }
            } ?: ColorConfig.Random
        }

    private val darkModeConfigFlow = dataStore
        .distinctKeyFlow(DarkModeConfigKey)
        .map { cachedValue ->
            cachedValue?.let { string ->
                Try { DarkModeConfig.valueOf(string) }
                    .getOrNull()
                    ?: DarkModeConfig.System
            } ?: DarkModeConfig.System
        }

    private val userIdFlow = flow {
        getUserId()
            .onSuccess {
                Try {
                    firebaseAnalytics.setUserId(it)
                }
                emit(it)
            }
            .onFailure {
                emit(generateUserId())
            }
            .logOnError("Failed to get user id")
    }

    private val userFlow = combine(
        userIdFlow,
        colorConfigFlow,
        darkModeConfigFlow,
        meGameStatsDao.getGameResultsFlow(),
        meGameStatsDao.getGamePlayedFlow(),
    ) { userId, colorConfig, darkModeConfig, meGameResults, meGamesPlayed ->
        User(
            id = userId,
            languageCode = Locale.getDefault().language,
            themeConfig = ThemeConfig(
                colorConfig = colorConfig,
                darkModeConfig = darkModeConfig
            ),
            stats = Stats(
                multiDeviceGamesPlayed = meGamesPlayed.filter { !it.wasSingleDevice }.size,
                winsAsOddOne = meGameResults.filter { it.didWin && it.wasOddOne }.map { it.gameKey },
                winsAsPlayer = meGameResults.filter { it.didWin && !it.wasOddOne }.map { it.gameKey },
                lossesAsOddOne = meGameResults.filter { !it.didWin && it.wasOddOne }.map { it.gameKey },
                lossesAsPlayer = meGameResults.filter { !it.didWin && !it.wasOddOne }.map { it.gameKey },
                singleDeviceGamesPlayed = meGamesPlayed.filter { it.wasSingleDevice }.size
                //TODO this filtering might be a little expensive
            )
        )
    }.stateIn(
        scope = applicationScope,
        started = SharingStarted.Eagerly,
        initialValue = User(
            id = null,
            languageCode = Locale.getDefault().language,
            themeConfig = defaultThemeConfig,
            stats = Stats(
                multiDeviceGamesPlayed = 0,
                winsAsOddOne = listOf(),
                winsAsPlayer = listOf(),
                lossesAsOddOne = listOf(),
                lossesAsPlayer = listOf(),
                singleDeviceGamesPlayed = 0
            )
        )
    )

    override fun getUserFlow(): Flow<User> = userFlow

    override suspend fun updateDarkModeConfig(darkModeConfig: DarkModeConfig) {
        cache(DarkModeConfigKey, darkModeConfig.name)
    }

    override suspend fun updateColorConfig(colorConfig: ColorConfig) {
        cache(ColorConfigKey, when (colorConfig) {
            is ColorConfig.Random -> RandomColorConfigValue
            is ColorConfig.Specific -> colorConfig.color.name
        })
    }

    // TODO store game stats backend and create sync operation
    override suspend fun addUsersGameResult(
        wasOddOneOut: Boolean,
        didWin: Boolean,
        accessCode: String,
        startedAt: Long
    ) {
        withContext(dispatcherProvider.io) {
            meGameStatsDao.addGameResult(
                result = MeGameResult(
                    gameKey = GameKey(accessCode, startedAt),
                    didWin = didWin,
                    wasOddOne = wasOddOneOut
                )
            )
        }
    }

    override suspend fun addGamePlayed(
        accessCode: String,
        startedAt: Long,
        wasSingleDevice: Boolean) {
        withContext(dispatcherProvider.io) {
            meGameStatsDao.addGamePlayed(
                gamePlayed = MeGamePlayed(
                    gameKey = GameKey(gameId = accessCode, startedAt = startedAt),
                    wasSingleDevice = wasSingleDevice,
                )
            )
        }
    }

    private suspend fun cache(key:  Preferences.Key<String>, value: String) {
        dataStore.updateData {
            it.toMutablePreferences()
                .apply {
                    this[key] = value
                }
        }
    }

    private suspend fun getUserId(): Try<String> = withBackoffRetry(
        retries = 2,
        initialDelayMillis = 500L,
        maxDelayMillis = 10.seconds.inWholeMilliseconds,
        factor = 2.0,
    ) {
        Try {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await().user?.uid ?: generateUserId()
            } else {
                auth.currentUser?.uid ?: generateUserId()
            }
        }
    }

    private fun generateUserId(): String = "GENERATED-" + UUID.randomUUID().toString()

    companion object {
        private const val RandomColorConfigValue = "RANDOM"
        private val defaultThemeConfig = ThemeConfig(
            colorConfig = ColorConfig.Random,
            darkModeConfig = DarkModeConfig.System
        )

        private val ColorConfigKey = stringPreferencesKey("theme_color")
        private val DarkModeConfigKey = stringPreferencesKey("dark_mode_config")
    }
}