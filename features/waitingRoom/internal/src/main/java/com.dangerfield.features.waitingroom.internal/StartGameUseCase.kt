package com.dangerfield.features.waitingroom.internal

import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.Player
import spyfallx.core.Try
import javax.inject.Inject
import javax.inject.Named

class StartGameUseCase @Inject constructor(
    private val packsPackRepository: LocationPackRepository,
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository
) {

    /**
     * Sets the flag for the game being started to true
     * assigns every player a role (including a random odd one out)
     * sets the started at time.
     */
    suspend operator fun invoke(
        accessCode: String,
        players: List<Player>,
        locationName: String
    ): Try<Unit> = Try {
        gameRepository
            .setGameIsBeingStarted(accessCode, true)
            .getOrThrow()

        // TODO cleanup
        // this logic is hella repeated, maybe an AssignRolesUseCase?
        val shuffledRoles = packsPackRepository.getRoles(locationName).getOrThrow().shuffled()
        val shuffledPlayers = players.shuffled()
        val oddOneOutIndex = shuffledPlayers.indices.random()

        val shuffledPlayersWithRoles = shuffledPlayers.mapIndexed { index, player ->
            val role = if (index == oddOneOutIndex) "The Odd One Out" else shuffledRoles[index]
            player.copy(role = role, isOddOneOut = index == oddOneOutIndex)
        }

        gameRepository
            .updatePlayers(accessCode, shuffledPlayersWithRoles)
            .flatMap { gameRepository.start(accessCode) }
            .eitherWay { gameRepository.setGameIsBeingStarted(accessCode, false) }
    }
}