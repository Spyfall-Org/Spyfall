package com.dangerfield.spyfall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.dangerfield.features.inAppMessaging.UpdateStatus
import com.dangerfield.libraries.coreflowroutines.waitFor
import com.dangerfield.libraries.dictionary.internal.ui.navigateToLanguageSupportDialog
import com.dangerfield.libraries.navigation.BuildNavHost
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.spyfall.MainActivityViewModel.Action.LoadConsentStatus
import com.dangerfield.spyfall.MainActivityViewModel.Action.MarkLanguageSupportLevelMessageShown
import com.dangerfield.spyfall.di.CompositionLocalsProvider
import com.dangerfield.spyfall.startup.SplashScreenBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import oddoneout.core.Message
import oddoneout.core.SnackBarPresenter
import oddoneout.core.doNothing
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var splashScreenBuilder: SplashScreenBuilder

    @Inject
    lateinit var buildNavHost: BuildNavHost

    @Inject
    lateinit var compositionLocalsProvider: CompositionLocalsProvider

    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isLoading: Boolean by mutableStateOf(true)

        splashScreenBuilder
            .keepOnScreenWhile { isLoading }
            .build(this)

        lifecycleScope.launch {
            // we do not set content loaded, this allows the splash screen to animate
            mainActivityViewModel.state.waitFor { !it.isBlockingLoad }
            isLoading = false
            setAppContent()
            loadConsentStatus()
        }
    }

    private fun loadConsentStatus() {
        mainActivityViewModel.takeAction(LoadConsentStatus(this))
    }

    private fun setAppContent() {
        setContent {
            val state by mainActivityViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state.languageSupportLevelMessage) {
                state.languageSupportLevelMessage?.let {
                    handleLanguageSupportMessage(it)
                }
            }

            LaunchedEffect(state.inAppUpdateStatus) {
                state.inAppUpdateStatus?.let {
                    handleInAppUpdateStatus(it)
                }
            }

            compositionLocalsProvider {
                OddOneOutApp(
                    isUpdateRequired = state.isUpdateRequired,
                    hasBlockingError = state.hasBlockingError,
                    accentColor = state.accentColor,
                    consentStatus = state.consentStatus,
                    isInMaintenanceMode = state.isInMaintenanceMode,
                    updateStatus = state.inAppUpdateStatus,
                    buildNavHost = { buildNavHost(it) }
                )
            }
        }
    }

    private fun handleLanguageSupportMessage(message: MainActivityViewModel.LanguageSupportLevelMessage?) {
        if (message != null) {

            mainActivityViewModel.takeAction(
                MarkLanguageSupportLevelMessageShown(
                    message.languageSupportLevel
                )
            )

            router.navigateToLanguageSupportDialog(
                supportLevelName = message.languageSupportLevel.name,
                languageDisplayName = message.languageSupportLevel.locale.displayLanguage
            )
        }
    }

    private fun handleInAppUpdateStatus(updateStatus: UpdateStatus) {
        when (updateStatus) {
            is UpdateStatus.UpdateAvailable -> {
                if (updateStatus.shouldUpdate) {
                    mainActivityViewModel.startInAppUpdate(this@MainActivity, updateStatus)
                }
            }

            is UpdateStatus.Downloaded -> {
                if (updateStatus.wasBackgroundUpdate) {
                    SnackBarPresenter.showMessage(
                        message = Message(
                            title = "Your update is ready",
                            message = "Your update has finished downloading. Tap install to use the newest version.",
                            autoDismiss = false,
                            actionLabel = "Install",
                            action = mainActivityViewModel::installUpdate
                        )
                    )
                }
            }

            is UpdateStatus.Failed,
            UpdateStatus.InvalidUpdateRequest -> {
                SnackBarPresenter.showMessage(
                    message = Message(
                        message = "We encountered a problem upgrading. Please try again in the app store.",
                        autoDismiss = true
                    )
                )
            }

            else -> doNothing()
        }
    }
}
