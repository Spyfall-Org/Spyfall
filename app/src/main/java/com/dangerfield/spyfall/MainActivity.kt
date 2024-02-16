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
import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.OddOneOutAd.GameRestartInterstitial
import com.dangerfield.features.ads.ui.InterstitialAd
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.OpenConsentForm
import com.dangerfield.libraries.analytics.MetricsTracker
import com.dangerfield.libraries.coreflowroutines.collectWhileStarted
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.navigation.BlockingScreenRouter
import com.dangerfield.libraries.network.NetworkMonitor
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import com.dangerfield.spyfall.startup.MainActivityViewModel
import com.dangerfield.spyfall.startup.SplashScreenBuilder
import dagger.hilt.android.AndroidEntryPoint
import oddoneout.core.BuildInfo
import oddoneout.core.doNothing
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private var hasSetContent = AtomicBoolean(false)
    private var hasLoadedInterstitialAd = AtomicBoolean(false)

    @Inject
    lateinit var navBuilderRegistry: NavBuilderRegistry

    @Inject
    lateinit var adsConfig: AdsConfig

    @Inject
    lateinit var buildInfo: BuildInfo

    @Inject
    lateinit var blockingScreenRouter: BlockingScreenRouter

    @Inject
    lateinit var metricsTracker: MetricsTracker

    @Inject
    lateinit var openConsentForm: OpenConsentForm

    @Inject
    lateinit var dictionary: Dictionary

    @Inject
    lateinit var splashScreenBuilder: SplashScreenBuilder

    @Inject
    lateinit var gameResetInterstitialAd: InterstitialAd<GameRestartInterstitial>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    // TODO cleanup: Completely remove legacy code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isLoading: Boolean by mutableStateOf(false)

        splashScreenBuilder
            .keepOnScreenWhile { isLoading }
            .build(this)

        // Delay set content so we can animate splash screen views
        collectWhileStarted(mainActivityViewModel.state) { state ->
            isLoading = state.isBlockingLoad
            if (!state.isBlockingLoad && !hasSetContent.getAndSet(true)) {
                setAppContent()
                loadConsentStatus()
            }
        }
    }

    private fun loadConsentStatus() {
        mainActivityViewModel.takeAction(MainActivityViewModel.Action.LoadConsentStatus(this))
    }

    private fun setAppContent() {
        setContent {
            val state by mainActivityViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state.consentStatus) {
                when (state.consentStatus) {
                    ConsentStatus.ConsentDenied,
                    ConsentStatus.ConsentNeeded -> openConsentForm()

                    ConsentStatus.ConsentGiven,
                    ConsentStatus.ConsentNotNeeded,
                    ConsentStatus.Unknown -> loadInterstitialAd()

                    null -> doNothing()
                }
            }

            OddOneOutApp(
                navBuilderRegistry = navBuilderRegistry,
                isUpdateRequired = state.isUpdateRequired,
                hasBlockingError = state.hasBlockingError,
                accentColor = state.accentColor,
                blockingScreenRouter = blockingScreenRouter,
                networkMonitor = networkMonitor,
                adsConfig = adsConfig,
                metricsTracker = metricsTracker,
                dictionary = dictionary,
                buildInfo = buildInfo,
                isInMaintenanceMode = state.isInMaintenanceMode,
                languageSupportLevelMessage = state.languageSupportLevelMessage,
                onLanguageSupportLevelMessageShown = {
                    mainActivityViewModel.takeAction(
                        MainActivityViewModel.Action.MarkLanguageSupportLevelMessageShown(
                            it
                        )
                    )
                }
            )
        }
    }

    private fun loadInterstitialAd() {
        if (!hasLoadedInterstitialAd.getAndSet(true)) {
            gameResetInterstitialAd.load(this)
        }
    }

    override fun onDestroy() {
        gameResetInterstitialAd.remove()
        super.onDestroy()
    }
}
