package com.dangerfield.libraries.coresession.internal

import android.util.Log
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.childSupervisorScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.session.ThemeConfig
import com.dangerfield.libraries.session.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import spyfallx.core.Try
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
    dispatcherProvider: DispatcherProvider,
) {

    private var userFlow = MutableStateFlow<User?>(null)

    init {
        applicationScope.childSupervisorScope(dispatcherProvider.io).launch {
            signIn()
                .eitherWay {
                    userFlow.tryEmit(
                        User(
                            id = auth.uid ?: UUID.randomUUID().toString(),
                            languageCode = Locale.getDefault().language,
                            themeConfig = defaultThemeConfig
                        )
                    )
                }
        }
    }

    fun getUserFlow(): Flow<User> = userFlow.filterNotNull()

    suspend fun getUser(): User = userFlow.filterNotNull().first()

    suspend fun updateUser(updater: User.() -> User?): Try<Unit> = Try {
        userFlow.update {
            val current = userFlow.filterNotNull().first()
            updater.invoke(current)
        }
    }

    private suspend fun signIn(): Try<Unit> = withBackoffRetry(
        retries = 2,
        initialDelayMillis = 500L,
        maxDelayMillis = 10.seconds.inWholeMilliseconds,
        factor = 2.0,
    ) {
        Try {
            if (auth.currentUser == null) auth.signInAnonymously().await()
        }
    }

    companion object {
        private val defaultThemeConfig = ThemeConfig(
            colorConfig = ColorConfig.Random,
            darkModeConfig = DarkModeConfig.System
        )
    }
}