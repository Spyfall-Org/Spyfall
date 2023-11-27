package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.internal.config.AccessCodeLength
import com.dangerfield.libraries.game.internal.config.MaxNameLength
import com.dangerfield.libraries.game.internal.config.MaxPlayers
import com.dangerfield.libraries.game.internal.config.MinNameLength
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class GameConfigImpl @Inject constructor(
    private val accessCodeLengthValue: AccessCodeLength,
    private val minNameLengthValue: MinNameLength,
    private val maxNameLengthValue: MaxNameLength,
    private val maxPlayersValue: MaxPlayers,
): GameConfig {

    override val accessCodeLength: Int
        get() = accessCodeLengthValue.resolveValue()

    override val minNameLength: Int
        get() = minNameLengthValue.resolveValue()

    override val maxNameLength: Int
        get() = maxNameLengthValue.resolveValue()

    override val maxPlayers: Int
        get() = maxPlayersValue.resolveValue()
}