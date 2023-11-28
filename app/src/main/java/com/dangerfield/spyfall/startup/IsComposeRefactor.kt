package com.dangerfield.spyfall.startup

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.Experiment
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class IsComposeRefactor @Inject constructor(
    private val mapBasedAppConfigMap: AppConfigMap,
): Experiment<Boolean>() {

    override val displayName: String = "Compose Refactor"
    override val description: String = "Changes will take effect on next app launch"
    override val id: String = "is_compose_refactor"

    override val control = false
    override val test =  true
    override val isDebugOnly = true
    override val default: Boolean = test

    override fun resolveValue(): Boolean = mapBasedAppConfigMap.experiment(this)
}