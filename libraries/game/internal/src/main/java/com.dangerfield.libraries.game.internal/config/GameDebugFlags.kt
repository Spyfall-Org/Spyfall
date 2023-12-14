package com.dangerfield.libraries.game.internal.config

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.Experiment
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ForceShortGames @Inject constructor(
    private val appConfigMap: AppConfigMap
): Experiment<Boolean>() {
    override val displayName: String
        get() = "Force Short Games"

    override val description: String
        get() = "When on games will be forces to be 10 seconds long"

    override val control: Boolean
        get() = false

    override val test: Boolean
        get() = true

    override val isDebugOnly: Boolean
        get() = true

    override fun resolveValue(): Boolean = appConfigMap.experiment(this)
}