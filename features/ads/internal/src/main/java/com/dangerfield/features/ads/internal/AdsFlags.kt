package com.dangerfield.features.ads.internal

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class IsWaitingRoomAdEnabledFlag @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Waiting Room Ad Enabled"
    override val path: String = "ads.isWaitingRoomAdEnabled"
    override val default: Boolean = true
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class IsMultiPlayerGamePlayAdEnabledFlag @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Multiplayer Gameplay Ad Enabled"
    override val path: String = "ads.isMultiPlayerGamePlayAdEnabled"
    override val default: Boolean = true
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class IsSingleDeviceGamePlayAdEnabledFlag @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Single Device Gameplay Ad Enabled"
    override val path: String = "ads.isSingleDeviceGamePlayAdEnabled"
    override val default: Boolean = true
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class IsSingleDeviceVotingAdEnabledFlag @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Single Device Voting Ad Enabled"
    override val path: String = "ads.isSingleDeviceVotingAdEnabled"
    override val default: Boolean = true
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class AreAllAdsDisabled @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Disable All Ads"
    override val path: String = "ads.disableAllAds"
    override val default: Boolean = false
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class IsSingleDeviceResultsAdEnabledFlag @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Single Device Results Ad Enabled"
    override val path: String = "ads.isSingleDeviceResultsAdEnabled"
    override val default: Boolean = true
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class IsRoleRevealAdEnabledFlag @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Role Reveal Ad Enabled"
    override val path: String = "ads.isRoleRevealAdEnabled"
    override val default: Boolean = true
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class IsGameRestartInterstitialAdEnabledFlag @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Game Restart Interstitial Ad Enabled"
    override val path: String = "ads.isGameRestartInterstitialAdEnabled"
    override val default: Boolean = false
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}

@AutoBindIntoSet
class GameResetInterstitialAdFrequency @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Game Restart Interstitial Ad Frequency"
    override val path: String = "ads.gameRestartInterstitialAdFrequency"
    override val default: Int = 3
    override val showInQADashboard = true
    override fun resolveValue(): Int = appConfigMap.value(this)
}
