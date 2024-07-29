package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GetGamePlayItems
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import oddoneout.core.Catching
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.illegalStateFailure
import oddoneout.core.throwIfDebug
import javax.inject.Inject

@AutoBind
class GetGamePlayItemsImpl @Inject constructor(
    private val gameConfig: GameConfig
) : GetGamePlayItems {
    override operator fun invoke(
        packs: List<Pack<PackItem>>,
        isSingleDevice: Boolean
    ): Catching<List<PackItem>> = Catching {

        val gamePlayItems = mutableSetOf<PackItem>()
        val packBank = packs.map { it.items.toMutableSet() }
        val totalItems = packBank.sumOf { it.size }

        var iteration = 0

        val numberOfItemsToChoose = if (isSingleDevice) {
            gameConfig.itemsPerSingleDeviceGame
        } else {
            gameConfig.itemsPerGame
        }

        if (numberOfItemsToChoose > totalItems) {
            return Catching.success(packBank.flatten().toList().shuffled())
        }

        while (gamePlayItems.size < numberOfItemsToChoose) {
            if (iteration > totalItems) {
                return illegalStateFailure {
                    """
                    Iterated through all locations in packs and still don't have enough locations for a game.
                    Packs: ${packs.map { "${it.name}: \n\n ${it.items.joinToString("\n")}" }}
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
                gamePlayItems.add(locationToAdd)
                locations.remove(locationToAdd)
            } ?: return illegalStateFailure {
                """
                   Location to add was null
                   Packs: ${packs.map { "${it.name}: \n\n ${it.items.joinToString("\n")}" }}
            """.trimIndent()
            }
                .throwIfDebug()

            iteration++
        }

        gamePlayItems.toList()
    }
}
