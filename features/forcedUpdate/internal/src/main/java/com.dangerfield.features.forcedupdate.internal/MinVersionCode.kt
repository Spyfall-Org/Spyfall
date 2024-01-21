package com.dangerfield.features.forcedupdate.internal

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class MinVersionCode@Inject constructor(
    private val appConfigMap: AppConfigMap
): ConfiguredValue<Int>() {
    override val displayName: String = "Min Version Code"
    override val path: String = "min_version_code"
    override val default: Int = 0
    override val showInQADashboard: Boolean = true
    override fun resolveValue() = appConfigMap.value(this)
}