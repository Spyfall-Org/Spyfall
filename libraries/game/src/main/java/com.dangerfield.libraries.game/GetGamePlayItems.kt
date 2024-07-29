package com.dangerfield.libraries.game

import oddoneout.core.Catching

/**
 * Gets a list of X locations to use for game play when creating or resetting a game.
 * Leverages the packs tied to the game to ensure a variety of locations are used.
 */
interface GetGamePlayItems {
    operator fun invoke(packs: List<Pack<PackItem>>, isSingleDevice: Boolean = false): Catching<List<PackItem>>
}
