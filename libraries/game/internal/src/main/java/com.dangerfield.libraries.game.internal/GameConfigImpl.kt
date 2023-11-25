package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.game.GameConfig
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class GameConfigImpl @Inject constructor(
    private val appConfigMap: AppConfigMap
): GameConfig {

    override val accessCodeLength: Int
        get() = appConfigMap.intValue("game", "accessCodeLength") ?: 6

    override val minNameLength: Int
        get() = appConfigMap.intValue("game", "nameLength") ?: 3

    override val maxNameLength: Int
        get() = appConfigMap.intValue("game", "nameLength") ?: 30

    override val maxPlayers: Int
        get() = appConfigMap.intValue("game", "maxPlayers") ?: 8
}