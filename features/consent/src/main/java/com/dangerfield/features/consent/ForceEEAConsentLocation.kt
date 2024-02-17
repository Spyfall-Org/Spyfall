package com.dangerfield.features.consent

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.Experiment
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ForceEEAConsentLocation @Inject constructor(
    private val appConfigMap: AppConfigMap
): Experiment<Boolean>() {
    override val displayName: String
        get() = "Force EEA Consent Location"

    override val id: String = "forceEEAConsentLocation"

    override val description: String
        get() = "Forces the EEA consent experience."

    override val control: Boolean
        get() = true

    override val showInQaExperiments: Boolean
        get() = false // will manually show in consent section

    override val test: Boolean
        get() = false

    override val isDebugOnly: Boolean
        get() = true

    override fun resolveValue(): Boolean = appConfigMap.experiment(this)
}