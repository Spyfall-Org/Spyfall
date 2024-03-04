package com.dangerfield.features.settings.internal.referral

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ReferralCodeLength @Inject constructor(
    private val appConfigMap: AppConfigMap
): ConfiguredValue<Int>() {
    override val displayName: String = "Referral Code Length"
    override val path: String = "referral_code_length"
    override val default: Int = 6
    override val showInQADashboard: Boolean = false
    override fun resolveValue() = appConfigMap.value(this)
}

@AutoBindIntoSet
class MaxReferralCodeRedemptions @Inject constructor(
    private val appConfigMap: AppConfigMap
): ConfiguredValue<Int>() {
    override val displayName: String = "Max Referral Code Redemptions"
    override val path: String = "max_referral_code_redemptions"
    override val default: Int = 1
    override val showInQADashboard: Boolean = false
    override fun resolveValue() = appConfigMap.value(this)
}