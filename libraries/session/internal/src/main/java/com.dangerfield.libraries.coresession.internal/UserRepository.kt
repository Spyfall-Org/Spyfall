package com.dangerfield.libraries.coresession.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.session.ThemeConfig
import com.dangerfield.libraries.session.User
import com.dangerfield.libraries.ui.color.ThemeColor
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.withBackoffRetry
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
) {

    private val updateMutex = Mutex()

    private val colorConfigFlow = dataStore.data.map { prefs ->
        prefs[ThemeColorKey]?.takeIf { it.isNotEmpty() }?.let { string ->
            Try { ColorConfig.Specific(ThemeColor.valueOf(string)) }
                .getOrNull()
                ?: ColorConfig.Random
        } ?: ColorConfig.Random
    }

    private val darkModeConfigFlow = dataStore.data.map {
        it[DarkModeConfigKey]?.let { string ->
            Try { DarkModeConfig.valueOf(string) }
                .getOrNull()
                ?: DarkModeConfig.System
        } ?: DarkModeConfig.System
    }

    private val userIdFlow = flow {
        getUserId()
            .onSuccess {
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
        darkModeConfigFlow
    ) { userId, colorConfig, darkModeConfig ->
        User(
            id = userId,
            languageCode = Locale.getDefault().language,
            themeConfig = ThemeConfig(
                colorConfig = colorConfig,
                darkModeConfig = darkModeConfig
            )
        )
    }.stateIn(
        scope = applicationScope,
        started = SharingStarted.Eagerly,
        initialValue = User(
            id = null,
            languageCode = Locale.getDefault().language,
            themeConfig = defaultThemeConfig
        )
    )

    fun getUserFlow(): Flow<User> = userFlow.filterNotNull()

    fun getUser(): User = userFlow.value

    suspend fun updateUser(updater: User.() -> User): Try<Unit> = Try {
        val updatedUser = updater.invoke(userFlow.value)

        updateMutex.withLock {
            dataStore.updateData {
                it.toMutablePreferences()
                    .apply {
                        this[ThemeColorKey] =
                            updatedUser.themeConfig.colorConfig.let { colorConfig ->
                                when (colorConfig) {
                                    is ColorConfig.Random -> ""
                                    is ColorConfig.Specific -> colorConfig.color.name
                                }
                            }
                        this[DarkModeConfigKey] = updatedUser.themeConfig.darkModeConfig.name
                    }
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
        private val defaultThemeConfig = ThemeConfig(
            colorConfig = ColorConfig.Random,
            darkModeConfig = DarkModeConfig.System
        )

        private val ThemeColorKey = stringPreferencesKey("theme_color")
        private val DarkModeConfigKey = stringPreferencesKey("dark_mode_config")
    }
}