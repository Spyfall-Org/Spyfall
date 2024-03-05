package com.dangerfield.features.inAppMessaging.internal.update

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject


@AutoBindIntoSet
class DaysBetweenInAppUpdateMessages @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Time Between Repeated In App Update Messages"
    override val path: String = "daysBetweenRepeatedInAppUpdateMessages"
    override val default: Int = 7
    override fun resolveValue(): Int = appConfigMap.value(this)
}
