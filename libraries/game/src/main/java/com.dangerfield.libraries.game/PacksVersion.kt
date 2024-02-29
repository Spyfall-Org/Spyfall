package com.dangerfield.libraries.game

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class PacksVersion @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Packs Version"
    override val path: String = "packs_version"
    override val default: Int = 0
    override fun resolveValue(): Int = appConfigMap.value(this)
}