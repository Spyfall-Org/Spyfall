package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.features.newgame.internal.presentation.model.CreateGameError
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.SessionRepository
import spyfallx.core.Try
import spyfallx.core.developerSnackIfDebug
import spyfallx.core.failure
import java.util.UUID
import javax.inject.Inject

class CreateGame @Inject constructor(
    private val generateAccessCode: GenerateAccessCode,
    private val gameRepository: GameRepository,
    private val getGamePlayLocations: GetGamePlayLocations,
    private val gameConfig: GameConfig,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(
        userName: String,
        packs: List<Pack>,
        timeLimit: Int,
        videoCallLink: String?
    ): Try<String> {
        return when {
            packs.isEmpty() -> CreateGameError.PacksEmpty.failure()
            timeLimit < gameConfig.minTimeLimit -> CreateGameError.TimeLimitTooShort.failure()
            timeLimit > gameConfig.maxTimeLimit -> CreateGameError.TimeLimitTooLong.failure()
            userName.isBlank() -> CreateGameError.NameBlank.failure()
            else -> create(
                userName = userName,
                packs = packs,
                timeLimit = timeLimit,
                videoCallLink = videoCallLink
            )
        }
    }

    private suspend fun create(
        userName: String,
        packs: List<Pack>,
        timeLimit: Int,
        videoCallLink: String?
    ): Try<String> = Try {
        // TODO log metric on this so we can tell how many multi device games there are created
        checkForExistingSession()

        val accessCode = generateAccessCode.invoke().getOrThrow()
        val locations = getGamePlayLocations(packs).getOrThrow()
        val userId = sessionRepository.session.user.id ?: UUID.randomUUID().toString()
        val currentPlayer = Player(
            id = userId,
            role = null,
            userName = userName,
            isHost = true,
            isOddOneOut = false,
            votedCorrectly = null
        )

        val game = Game(
            locationName = locations.random().name,
            packNames = packs.map { it.name },
            isBeingStarted = false,
            players = listOf(currentPlayer),
            timeLimitMins = timeLimit,
            startedAt = null,
            locationOptionNames = locations.map { it.name },
            videoCallLink = videoCallLink,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode
        )

        gameRepository.create(game)
            .map { accessCode }
            .onSuccess {
                sessionRepository.updateActiveGame(
                    ActiveGame(
                        accessCode = accessCode,
                        userId = userId
                    )
                )
            }
            .getOrThrow()
    }

    private suspend fun checkForExistingSession() {
        if (sessionRepository.session.activeGame != null) {
            sessionRepository.updateActiveGame(null)
            developerSnackIfDebug {
                "User is already in an existing game while joining a game."
            }
        }
    }

}