package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.features.newgame.internal.presentation.model.CreateGameError
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.Player
import spyfallx.core.Try
import spyfallx.core.failure
import java.util.UUID
import javax.inject.Inject

class CreateGame @Inject constructor(
    private val generateAccessCode: GenerateAccessCode,
    private val gameRepository: GameRepository,
    private val getGamePlayLocations: GetGamePlayLocations,
    private val gameConfig: GameConfig
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
            else -> createGame(
                userName = userName,
                packs = packs,
                timeLimit = timeLimit,
                videoCallLink = videoCallLink
            )
        }
    }

    private suspend fun createGame(
        userName: String,
        packs: List<Pack>,
        timeLimit: Int,
        videoCallLink: String?
    ): Try<String> = Try {
        // TODO log metric on this so we can tell how many multi device games there are created
        val accessCode = generateAccessCode.invoke().getOrThrow()
        val locations = getGamePlayLocations(packs).getOrThrow()
        val currentPlayer = Player(
            id = UUID.randomUUID().toString(),
            role = null,
            userName = userName,
            isHost = true,
            isSpy = false
        )

        val game = Game(
            location = locations.random().name,
            packNames = packs.map { it.name },
            hasStarted = false,
            players = listOf(currentPlayer),
            timeLimitMins = timeLimit,
            startedAt = null,
            locations = locations.map { it.name },
            videoCallLink = videoCallLink,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode
        )

        gameRepository.create(game)
            .map { accessCode }
            .getOrThrow()
    }

}