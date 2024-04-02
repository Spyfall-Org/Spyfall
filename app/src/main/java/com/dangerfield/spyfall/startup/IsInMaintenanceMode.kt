package com.dangerfield.spyfall.startup

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

/**
 * This flag allows the app to be put into a mode where it will be temporarily unavailable.
 * When true the user will see [com.dangerfield.features.blockingerror.internal.MaintenanceModeScreen]
 */
@AutoBindIntoSet
class IsInMaintenanceMode @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Boolean>() {
    override val displayName: String = "Is In Maintenance Mode"
    override val path: String = "isInMaintenanceMode"
    override val default: Boolean = false
    override val showInQADashboard = true
    override fun resolveValue(): Boolean = appConfigMap.value(this)
}