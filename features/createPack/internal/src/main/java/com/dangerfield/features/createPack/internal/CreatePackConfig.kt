package com.dangerfield.features.createPack.internal

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class MaxCustomPackItemCount @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Maximum Custom Pack Items"
    override val path: String = "createPack.maximumItems"
    override val default: Int = 15
    override val showInQADashboard: Boolean = true
    override fun resolveValue(): Int = appConfigMap.value(this)
}