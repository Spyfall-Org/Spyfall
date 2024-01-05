package com.dangerfield.features.ads

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAdsConfig = staticCompositionLocalOf<AdsConfig> {
    error("No LocalAdsConfig provided")
}

val NoOpAdsConfig = object : AdsConfig {
    override val isWaitingRoomAdEnabled: Boolean
        get() = true
    override val isMultiPlayerGamePlayAdEnabled: Boolean
        get() = true
    override val isSingleDeviceGamePlayAdEnabled: Boolean
        get() = true
    override val isSingleDeviceVotingAdEnabled: Boolean
        get() = true
    override val isSingleDeviceResultsAdEnabled: Boolean
        get() = true
    override val isRoleRevealAdEnabled: Boolean
        get() = true
    override val isGameRestartInterstitialAdEnabled: Boolean
        get() = true
    override val gameRestInterstitialAdFrequency: Int
        get() = 3

    override fun isAdEnabled(ad: OddOneOutAd): Boolean {
        return true
    }
}