package com.dangerfield.features.ads

interface AdsConfig {
    val isWaitingRoomAdEnabled: Boolean
    val isMultiPlayerGamePlayAdEnabled: Boolean
    val isSingleDeviceGamePlayAdEnabled: Boolean
    val isSingleDeviceVotingAdEnabled: Boolean
    val isSingleDeviceResultsAdEnabled: Boolean
    val isRoleRevealAdEnabled: Boolean
    val isGameRestartInterstitialAdEnabled: Boolean
    val gameRestInterstitialAdFrequency: Int

    fun isAdEnabled(ad: OddOneOutAd): Boolean
}