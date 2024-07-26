package com.dangerfield.libraries.game

import oddoneout.core.Catching

/**
 * Gets a list of X locations to use for game play when creating or resetting a game.
 * Leverages the packs tied to the game to ensure a variety of locations are used.
 */
interface GetGamePlayLocations {
    operator fun invoke(locationPacks: List<LocationPack>, isSingleDevice: Boolean = false): Catching<List<OldLocationModel>>
}