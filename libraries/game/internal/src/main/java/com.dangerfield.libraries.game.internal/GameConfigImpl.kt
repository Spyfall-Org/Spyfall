package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.internal.config.AccessCodeLength
import com.dangerfield.libraries.game.internal.config.LocationsPerGame
import com.dangerfield.libraries.game.internal.config.MaxNameLength
import com.dangerfield.libraries.game.internal.config.MaxPlayers
import com.dangerfield.libraries.game.internal.config.MaxTimeLimit
import com.dangerfield.libraries.game.internal.config.MinNameLength
import com.dangerfield.libraries.game.internal.config.MinPlayers
import com.dangerfield.libraries.game.internal.config.MinTimeLimit
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class GameConfigImpl @Inject constructor(
    private val accessCodeLengthValue: AccessCodeLength,
    private val minNameLengthValue: MinNameLength,
    private val maxNameLengthValue: MaxNameLength,
    private val maxPlayersValue: MaxPlayers,
    private val maxTimeLimitValue: MaxTimeLimit,
    private val minTimeLimitValue: MinTimeLimit,
    private val minPlayersValue: MinPlayers,
    private val locationsPerGameValue: LocationsPerGame,
    ): GameConfig {

    override val accessCodeLength: Int
        get() = accessCodeLengthValue.resolveValue()

    override val minNameLength: Int
        get() = minNameLengthValue.resolveValue()

    override val maxNameLength: Int
        get() = maxNameLengthValue.resolveValue()

    override val maxPlayers: Int
        get() = maxPlayersValue.resolveValue()

    override val maxTimeLimit: Int
        get() = maxTimeLimitValue.resolveValue()

    override val minTimeLimit: Int
        get() = minTimeLimitValue.resolveValue()

    override val minPlayers: Int
        get() = minPlayersValue.resolveValue()

    override val locationsPerGame: Int
        get() = locationsPerGameValue.resolveValue()
}