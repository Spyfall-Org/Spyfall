package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.internal.config.AccessCodeLength
import com.dangerfield.libraries.game.internal.config.ForceShortGames
import com.dangerfield.libraries.game.internal.config.InactivityExpirationMins
import com.dangerfield.libraries.game.internal.config.IsSingleDeviceModeEnabled
import com.dangerfield.libraries.game.internal.config.LocationsPerGame
import com.dangerfield.libraries.game.internal.config.LocationsPerSingleDeviceGame
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
    private val inactivityExpirationMinsValue: InactivityExpirationMins,
    private val locationsPerGameValue: LocationsPerGame,
    private val locationsPerSingleDeviceGameValue: LocationsPerSingleDeviceGame,
    private val isSingleDeviceModeEnabledExperiment: IsSingleDeviceModeEnabled,
    private val forceShortGamesFlag: ForceShortGames
    ): GameConfig {

    override val accessCodeLength: Int
        get() = accessCodeLengthValue()

    override val minNameLength: Int
        get() = minNameLengthValue()

    override val maxNameLength: Int
        get() = maxNameLengthValue()

    override val maxPlayers: Int
        get() = maxPlayersValue()

    override val maxTimeLimit: Int
        get() = maxTimeLimitValue()

    override val minTimeLimit: Int
        get() = minTimeLimitValue()

    override val minPlayers: Int
        get() = minPlayersValue()

    override val locationsPerGame: Int
        get() = locationsPerGameValue()

    override val isSingleDeviceModeEnabled: Boolean
        get() = isSingleDeviceModeEnabledExperiment()

    override val forceShortGames: Boolean
        get() = forceShortGamesFlag()

    override val gameInactivityExpirationMins: Int
        get() = inactivityExpirationMinsValue()

    override val locationsPerSingleDeviceGame: Int
        get() = locationsPerSingleDeviceGameValue()
}