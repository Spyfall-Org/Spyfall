package com.dangerfield.features.ads.ui

import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.OddOneOutAd.GameRestartInterstitial
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdsModule {

    @Singleton
    @Provides
    fun providesGameResetInterstitialAd(
        adsConfig: AdsConfig,
        @ApplicationScope applicationScope: CoroutineScope,
        dispatcherProvider: DispatcherProvider
    ): InterstitialAd<GameRestartInterstitial> {
        return InterstitialAd(
            ad = GameRestartInterstitial,
            adsConfig = adsConfig,
            applicationScope = applicationScope,
            dispatcherProvider = dispatcherProvider
        )
    }
}