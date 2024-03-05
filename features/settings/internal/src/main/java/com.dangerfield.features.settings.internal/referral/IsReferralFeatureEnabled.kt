package com.dangerfield.features.settings.internal.referral

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class IsReferralFeatureEnabled @Inject constructor(
    private val appConfigMap: AppConfigMap
): ConfiguredValue<Boolean>() {
    override val displayName: String = "Referral Feature Enabled"
    override val path: String = "is_referral_feature_enabled"
    override val default: Boolean = false
    override val showInQADashboard: Boolean = true
    override fun resolveValue() = appConfigMap.value(this)
}