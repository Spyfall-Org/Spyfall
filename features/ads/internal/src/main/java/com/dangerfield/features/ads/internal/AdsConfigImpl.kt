package com.dangerfield.features.ads.internal

import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.OddOneOutAd
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class AdsConfigImpl @Inject constructor(
    private val isWaitingRoomAdEnabledFlag: IsWaitingRoomAdEnabledFlag,
    private val isMultiPlayerGamePlayAdEnabledFlag: IsMultiPlayerGamePlayAdEnabledFlag,
    private val isSingleDeviceGamePlayAdEnabledFlag: IsSingleDeviceGamePlayAdEnabledFlag,
    private val isSingleDeviceVotingAdEnabledFlag: IsSingleDeviceVotingAdEnabledFlag,
    private val isSingleDeviceResultsAdEnabledFlag: IsSingleDeviceResultsAdEnabledFlag,
    private val isRoleRevealAdEnabledFlag: IsRoleRevealAdEnabledFlag,
    private val isGameRestartInterstitialAdEnabledFlag: IsGameRestartInterstitialAdEnabledFlag,
    private val gameResetInterstitialAdFrequencyConfig: GameResetInterstitialAdFrequency,
    private val areAllAdsDisabled: AreAllAdsDisabled
) : AdsConfig {
    override val isWaitingRoomAdEnabled: Boolean
        get() = isWaitingRoomAdEnabledFlag.value

    override val isMultiPlayerGamePlayAdEnabled: Boolean
        get() = isMultiPlayerGamePlayAdEnabledFlag.value

    override val isSingleDeviceGamePlayAdEnabled: Boolean
        get() = isSingleDeviceGamePlayAdEnabledFlag.value

    override val isSingleDeviceVotingAdEnabled: Boolean
        get() = isSingleDeviceVotingAdEnabledFlag.value

    override val isSingleDeviceResultsAdEnabled: Boolean
        get() = isSingleDeviceResultsAdEnabledFlag.value

    override val isRoleRevealAdEnabled: Boolean
        get() = isRoleRevealAdEnabledFlag.value

    override val isGameRestartInterstitialAdEnabled: Boolean
        get() = isGameRestartInterstitialAdEnabledFlag.value

    override val gameRestInterstitialAdFrequency: Int
        get() = gameResetInterstitialAdFrequencyConfig.value

    override fun isAdEnabled(ad: OddOneOutAd) = if (areAllAdsDisabled.value) false else
        when (ad) {
            OddOneOutAd.SingleDeviceVoting -> isSingleDeviceVotingAdEnabled
            OddOneOutAd.SingleDeviceResults -> isSingleDeviceResultsAdEnabled
            OddOneOutAd.RoleRevealBanner -> isRoleRevealAdEnabled
            OddOneOutAd.WaitingRoomBanner -> isWaitingRoomAdEnabled
            OddOneOutAd.MultiPlayerGamePlayBanner -> isMultiPlayerGamePlayAdEnabled
            OddOneOutAd.SingleDeviceGamePlayBanner -> isSingleDeviceGamePlayAdEnabled
            OddOneOutAd.GameRestartInterstitial -> isGameRestartInterstitialAdEnabled
        }
}