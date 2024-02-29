package com.dangerfield.libraries.dictionary.internal

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import java.util.Locale
import javax.inject.Inject

@AutoBindIntoSet
class ConfigDictionary @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Map<String, String>>() {
    private val languageCode = Locale.getDefault().language

    override val displayName: String = "Override Dictionary"
    override val path: String = "dictionary_overrides.$languageCode"
    override val default: Map<String, String> = emptyMap()
    override val showInQADashboard = false
    override fun resolveValue(): Map<String, String> = appConfigMap.value(this)
}