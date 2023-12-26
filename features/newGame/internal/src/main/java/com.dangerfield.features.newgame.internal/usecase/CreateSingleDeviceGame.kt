package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateActiveGame
import spyfallx.core.Try
import spyfallx.core.developerSnackIfDebug
import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

@Suppress("UnusedPrivateMember")
class CreateSingleDeviceGame @Inject constructor(
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val getGamePlayLocations: GetGamePlayLocations,
    private val clock: Clock,
    private val generateAccessCode: GenerateAccessCode,
    private val updateActiveGame: UpdateActiveGame,
    private val clearActiveGame: ClearActiveGame,
    private val session: Session,
) {

    suspend operator fun invoke(
        packs: List<Pack>,
        timeLimit: Int,
        numOfPlayers: Int,
    ): Try<String> = Try {
        // TODO log metric on this so we can tell how many multi device games there are created
        checkForExistingSession()

        val accessCode = generateAccessCode().getOrThrow()
        val locations = getGamePlayLocations(packs = packs, isSingleDevice = true).getOrThrow()
        val location = locations.random()
        val userId = session.user.id ?: UUID.randomUUID().toString()
        val shuffledRoles = location.roles.shuffled()

        val host = Player(
            id = userId,
            role = null,
            userName = "Player 1",
            isHost = true,
            isOddOneOut = false,
            votedCorrectly = null
        )

        val players =
            listOf(host) +
                    (2..numOfPlayers).map {
                        Player(
                            id = UUID.randomUUID().toString(),
                            role = null,
                            userName = "Player $it",
                            isHost = false,
                            isOddOneOut = false,
                            votedCorrectly = null
                        )
                    }

        val oddOneOutIndex = players.indices.random()

        val playersWithRoles = players.mapIndexed { index, player ->
            val role = if (index == oddOneOutIndex) "The Odd One Out" else shuffledRoles[index]
            player.copy(role = role, isOddOneOut = index == oddOneOutIndex)
        }

        val game = Game(
            locationName = location.name,
            packNames = packs.map { it.name },
            isBeingStarted = false,
            players = playersWithRoles,
            timeLimitMins = timeLimit,
            startedAt = null,
            locationOptionNames = locations.map { it.name },
            videoCallLink = null,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis()
        )

        gameRepository.create(game)
            .map { accessCode }
            .onSuccess {
                updateActiveGame(
                    ActiveGame(
                        accessCode = accessCode,
                        userId = userId,
                        isSingleDevice = true
                    )
                )
            }
            .getOrThrow()
    }

    private suspend fun checkForExistingSession() {
        if (session.activeGame != null) {
            clearActiveGame()
            developerSnackIfDebug {
                "User is already in an existing game while creating a game."
            }
        }
    }
}