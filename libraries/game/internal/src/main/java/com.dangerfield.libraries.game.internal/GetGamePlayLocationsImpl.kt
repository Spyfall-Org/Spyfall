package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
import oddoneout.core.Catching
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.illegalStateFailure
import oddoneout.core.throwIfDebug
import javax.inject.Inject

@AutoBind
class GetGamePlayLocationsImpl @Inject constructor(
    private val gameConfig: GameConfig
) : GetGamePlayLocations {
    override operator fun invoke(
        locationPacks: List<LocationPack>,
        isSingleDevice: Boolean
    ): Catching<List<Location>> = Catching {

        val gamePlayLocations = mutableSetOf<Location>()
        val packBank = locationPacks.map { it.locations.toMutableSet() }
        val totalLocations = packBank.sumOf { it.size }

        var iteration = 0

        val numOfLocationsToChoose = if (isSingleDevice) {
            gameConfig.locationsPerSingleDeviceGame
        } else {
            gameConfig.locationsPerGame
        }

        if (numOfLocationsToChoose > totalLocations) {
            return Catching.success(packBank.flatten().toList().shuffled())
        }

        while (gamePlayLocations.size < numOfLocationsToChoose) {
            if (iteration > totalLocations) {
                return illegalStateFailure {
                    """
                    Iterated through all locations in packs and still don't have enough locations for a game.
                    Packs: ${locationPacks.map { "${it.name}: \n\n ${it.locations.joinToString("\n")}" }}
                """.trimIndent()
                }
                    .throwIfDebug()
            }

            // TODO non empty packs size can be 0 here
            /*
            This situation can occur if all packs become empty during the loop execution
             */
            val nonEmptyPacks = packBank.filter { it.isNotEmpty() }
            val packIndex = iteration % nonEmptyPacks.size
            val locations = nonEmptyPacks[packIndex]

            locations.randomOrNull()?.let { locationToAdd ->
                gamePlayLocations.add(locationToAdd)
                locations.remove(locationToAdd)
            } ?: return illegalStateFailure {
                """
                   Location to add was null
                   Packs: ${locationPacks.map { "${it.name}: \n\n ${it.locations.joinToString("\n")}" }}
            """.trimIndent()
            }
                .throwIfDebug()

            iteration++
        }

        gamePlayLocations.toList()
    }
}
