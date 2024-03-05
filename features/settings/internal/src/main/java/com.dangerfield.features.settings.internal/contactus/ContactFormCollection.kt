package com.dangerfield.features.settings.internal.contactus

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ContactFormCollection @Inject constructor(
    private val appConfigMap: AppConfigMap
): ConfiguredValue<String>() {
    override val displayName: String = "Contact Form Collection"
    override val path: String = "contact_form_collection"
    override val default: String = "contact-forms"
    override val showInQADashboard: Boolean = true
    override fun resolveValue() = appConfigMap.value(this)
}