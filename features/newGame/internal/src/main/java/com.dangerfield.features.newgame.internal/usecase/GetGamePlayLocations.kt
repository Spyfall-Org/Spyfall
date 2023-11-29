package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.Pack
import spyfallx.core.Try
import spyfallx.core.failure
import spyfallx.core.throwIfDebug
import javax.inject.Inject

class GetGamePlayLocations @Inject constructor(
    private val gameConfig: GameConfig
) {
    operator fun invoke(packs: List<Pack>): Try<List<Location>> = Try {

        val gamePlayLocations = mutableSetOf<Location>()
        val packBank = packs.map { it.locations.toMutableSet() }
        val totalLocations = packBank.sumOf { it.size }

        var iteration = 0

        while (gamePlayLocations.size < gameConfig.locationsPerGame) {
            if (iteration > totalLocations) {
                return IllegalStateException(
                    """
                    Iterated through all locations in packs and still don't have enough locations for a game.
                    Packs: ${packs.map { "${it.name}: \n\n ${it.locations.joinToString("\n")}" }}
                """.trimIndent()
                )
                    .failure()
                    .throwIfDebug()
            }

            val nonEmptyPacks = packBank.filter { it.isNotEmpty() }
            val packIndex = iteration % nonEmptyPacks.size
            val locations = nonEmptyPacks[packIndex]

            locations.randomOrNull()?.let { locationToAdd ->
                gamePlayLocations.add(locationToAdd)
                locations.remove(locationToAdd)
            } ?: return IllegalStateException(
                """
                   Location to add was null
                   Packs: ${packs.map { "${it.name}: \n\n ${it.locations.joinToString("\n")}" }}
            """.trimIndent()
            )
                .failure()
                .throwIfDebug()

            iteration++
        }

        gamePlayLocations.toList()
    }
}
