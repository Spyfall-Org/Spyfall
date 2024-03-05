package com.dangerfield.libraries.dictionary.internal

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import java.util.Locale
import javax.inject.Inject

@AutoBindIntoSet
class CurrentLanguageSupportLevelString @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<String>() {
    private val languageCode = Locale.getDefault().language

    override val displayName: String = "Language Support Level"
    override val path: String = "languageSupportLevels.$languageCode"
    override val default: String = "unknown"
    override val showInQADashboard = true
    override fun resolveValue(): String = appConfigMap.value(this)
}