package com.dangerfield.libraries.session

import oddoneout.core.Catching

/**
 * As games are played we cache details about the active game such that if the app is killed,
 * when the user returns we can take them back to their game (given that its still ongoing)
 *
 * Clearing the active game removes that game from cache.
 */
interface ClearActiveGame {
    suspend operator fun invoke(): Catching<Unit>
}