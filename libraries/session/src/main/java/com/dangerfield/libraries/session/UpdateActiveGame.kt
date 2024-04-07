package com.dangerfield.libraries.session

import oddoneout.core.Catching

/**
 * Updates the active game in cache
 *
 * As games are played we cache details about the active game such that if the app is killed,
 * when the user returns we can take them back to their game (given that its still ongoing)
 */
interface UpdateActiveGame {
    suspend operator fun invoke(activeGame: ActiveGame): Catching<Unit>
}