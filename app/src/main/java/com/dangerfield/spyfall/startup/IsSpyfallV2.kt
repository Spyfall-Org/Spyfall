package com.dangerfield.spyfall.startup

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.Experiment
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class IsSpyfallV2 @Inject constructor(
    private val mapBasedAppConfigMap: AppConfigMap,
): Experiment<Boolean>() {

    override val displayName: String = "Spyfall V2"
    override val id: String = "isSpyfallV2"

    override val control = false
    override val test =  true
    override val isDebugOnly = true
    override val default: Boolean = test

    override fun resolveValue(): Boolean = mapBasedAppConfigMap.experiment(this)
}