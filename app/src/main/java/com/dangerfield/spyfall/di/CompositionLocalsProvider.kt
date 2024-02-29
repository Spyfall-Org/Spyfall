package com.dangerfield.spyfall.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.LocalAdsConfig
import com.dangerfield.libraries.analytics.LocalMetricsTracker
import com.dangerfield.libraries.analytics.MetricsTracker
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.network.NetworkMonitor
import com.dangerfield.libraries.ui.LocalAppState
import com.dangerfield.libraries.ui.LocalBuildInfo
import com.dangerfield.spyfall.rememberAppState
import oddoneout.core.BuildInfo
import javax.inject.Inject

/**
 * Provides all the composition locals for the app
 * If a module needs to provide a local, it should expose the local in the UI library and
 * bind the implementation to be injectable here. Then all composables will have access
 */
class CompositionLocalsProvider @Inject constructor(
    private val adsConfig: AdsConfig,
    private val metricsTracker: MetricsTracker,
    private val buildInfo: BuildInfo,
    private val dictionary: Dictionary,
    private val networkMonitor: NetworkMonitor
){

    @Composable
    operator fun invoke(content: @Composable () -> Unit) {

        val appState = rememberAppState(networkMonitor = networkMonitor)

        CompositionLocalProvider(
            LocalAdsConfig provides adsConfig,
            LocalMetricsTracker provides metricsTracker,
            LocalDictionary provides dictionary,
            LocalBuildInfo provides buildInfo,
            LocalAppState provides appState,
            content = content
        )
    }
}