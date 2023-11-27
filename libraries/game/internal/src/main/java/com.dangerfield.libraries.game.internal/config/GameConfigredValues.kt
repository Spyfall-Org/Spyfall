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
    override val default: Int = 3
    override fun resolveValue(): Int = appConfigMap.value(this)
}

@AutoBindIntoSet
class MaxNameLength @Inject constructor(
    private val appConfigMap: AppConfigMap
) : ConfiguredValue<Int>() {
    override val displayName: String = "Maximum Name Length Length"
    override val path: String = "game.maxNameLength"
    override val default: Int = 3
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
