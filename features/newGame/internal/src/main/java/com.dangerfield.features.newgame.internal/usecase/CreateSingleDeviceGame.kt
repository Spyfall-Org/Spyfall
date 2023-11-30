package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.Pack
import spyfallx.core.Try
import spyfallx.core.success
import javax.inject.Inject

@Suppress("UnusedPrivateMember")
class CreateSingleDeviceGame @Inject constructor(
    private val gameConfig: GameConfig,
    private val getGamePlayLocations: GetGamePlayLocations
) {

    suspend operator fun invoke(
        packs: List<Pack>,
        timeLimit: Int,
        numOfPlayers: Int,
    ): Try<Unit> {
        return when {
            else -> {
                //TODO log metric on this so we can tell how many single device games there are created
                // TODO create a SingleDeviceGame object, this function should return that object
                // Then from there it can be passed through or upward
                val locations = getGamePlayLocations(packs)
                Unit.success()
            }
        }
    }
}