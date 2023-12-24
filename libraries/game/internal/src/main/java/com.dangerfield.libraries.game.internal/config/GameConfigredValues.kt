package com.dangerfield.libraries.game.internal.config

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class AccessCodeLength @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Access Code Length"
    override val path: String = "game.accessCodeLength"
    override val default: Int = 6
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class MinNameLength @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Minimum Name Length Length"
    override val path: String = "game.minNameLength"
    override val default: Int = 2
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class MaxNameLength @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Maximum Name Length Length"
    override val path: String = "game.maxNameLength"
    override val default: Int = 30
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class MaxPlayers @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Maximum Players"
    override val path: String = "game.maxPlayers"
    override val default: Int = 8
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class MinPlayers @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Min Players"
    override val path: String = "game.minPlayers"
    override val default: Int = 3
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class InactivityExpirationMins @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Inactivity Expiration Mins"
    override val path: String = "game.inactivityExpirationMins"
    override val default: Int = 30
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class LocationsPerGame @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Locations Per Game"
    override val path: String = "game.locationsPerGame"
    override val default: Int = 14
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class LocationsPerSingleDeviceGame @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Locations Per Single Device Game"
    override val path: String = "game.locationsPerSingleDeviceGame"
    override val default: Int = 6
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class MinTimeLimit @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Minimum Game Time Limit"
    override val path: String = "game.minTimeLimit"
    override val default: Int = 2
    override val debugOverride: Int = 1
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class MaxTimeLimit @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Max Game Time Limit"
    override val path: String = "game.maxTimeLimit"
    override val default: Int = 10
    override fun resolveValue(): Int = appConfigMap.value(this)
}
