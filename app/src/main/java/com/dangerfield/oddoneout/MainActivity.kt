package com.dangerfield.oddoneout

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.OddOneOutAd.GameRestartInterstitial
import com.dangerfield.features.ads.ui.InterstitialAd
import com.dangerfield.libraries.analytics.MetricsTracker
import com.dangerfield.libraries.coreflowroutines.collectWhileStarted
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.network.NetworkMonitor
import com.dangerfield.oddoneout.navigation.NavBuilderRegistry
import com.dangerfield.oddoneout.startup.MainActivityViewModel
import com.dangerfield.oddoneout.startup.SplashScreenBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private var hasSetContent = AtomicBoolean(false)

    @Inject
    lateinit var navBuilderRegistry: NavBuilderRegistry

    @Inject
    lateinit var adsConfig: AdsConfig

    @Inject
    lateinit var metricsTracker: MetricsTracker

    @Inject
    lateinit var dictionary: Dictionary

    @Inject
    lateinit var gameResetInterstitialAd: InterstitialAd<GameRestartInterstitial>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    // TODO cleanup: Completely remove legacy code

    override fun onCreate(savedInstanceState: Bundle?) {
        var isLoading: Boolean by mutableStateOf(false)

        SplashScreenBuilder(this)
            .keepOnScreenWhile { isLoading }
            .build()

        super.onCreate(savedInstanceState) // should be called after splash screen builder

        //WindowCompat.setDecorFitsSystemWindows(window, false)

        // Delay set content so we can animate splash screen views
        collectWhileStarted(mainActivityViewModel.state) { state ->
            isLoading = state.isBlockingLoad
            if (!state.isBlockingLoad && !hasSetContent.getAndSet(true)) {
                setAppContent()
            }
        }
    }

    private fun setAppContent() {
        setContent {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            val state by mainActivityViewModel.state.collectAsStateWithLifecycle()

            // TODO make the app still usable when there is an error.
            // we dont need a session and such to let the user use the app.
            OddOneOutApp(
                navBuilderRegistry = navBuilderRegistry,
                isUpdateRequired = state.isUpdateRequired,
                hasBlockingError = state.hasBlockingError,
                accentColor = state.accentColor,
                darkModeConfig = state.darkModeConfig,
                networkMonitor = networkMonitor,
                adsConfig = adsConfig,
                metricsTracker = metricsTracker,
                dictionary = dictionary,
                legalAcceptanceState = state.legalAcceptanceState,
                languageSupportLevel = state.languageSupportLevel
            )
        }

        gameResetInterstitialAd.load(this@MainActivity)
    }

    override fun onDestroy() {
        gameResetInterstitialAd.remove()
        super.onDestroy()
    }

}
