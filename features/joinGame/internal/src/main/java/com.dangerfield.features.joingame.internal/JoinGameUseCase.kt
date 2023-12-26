package com.dangerfield.features.joingame.internal

import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateActiveGame
import spyfallx.core.Try
import spyfallx.core.developerSnackIfDebug
import spyfallx.core.failure
import spyfallx.core.success
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

class JoinGameUseCase @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    private val gameConfig: GameConfig,
    private val session: Session,
    private val updateActiveGame: UpdateActiveGame,
    private val clearActiveGame: ClearActiveGame
) {
    suspend operator fun invoke(
        accessCode: String, userName: String
    ): Try<Game> {

        checkForExistingSession()

        val id = session.user.id ?: UUID.randomUUID().toString()

        return if (accessCode.length < gameConfig.accessCodeLength || accessCode.length > gameConfig.accessCodeLength) {
            JoinGameError.InvalidAccessCodeLength(requiredLength = gameConfig.accessCodeLength)
                .failure()
        } else if (userName.length > gameConfig.maxNameLength || userName.length < gameConfig.minNameLength) {
            JoinGameError.InvalidNameLength(
                min = gameConfig.minNameLength, max = gameConfig.maxNameLength
            ).failure()
        } else {
            gameRepository.getGame(accessCode).fold(onSuccess = { game ->
                val joinGameError = getGameStateError(accessCode, game, userName)

                if (joinGameError != null) {
                    joinGameError.failure()
                } else {
                    joinGame(
                        accessCode = accessCode,
                        userName = userName,
                        game = game,
                        id = id
                    )
                }
            }, onFailure = {
                it.toJoinGameFailure()
            }).onSuccess {
                updateSession(
                    userId = id,
                    accessCode = accessCode
                )
            }
        }
    }

    private fun getGameStateError(
        accessCode: String,
        game: Game,
        userName: String
    ): JoinGameError? {
        val gameState = mapToGameState(accessCode, game)

        return when {
            gameState is GameState.DoesNotExist -> JoinGameError.GameNotFound
            gameState is GameState.Started -> JoinGameError.GameAlreadyStarted
            gameState is GameState.Starting -> JoinGameError.GameAlreadyStarted
            gameState is GameState.Voting -> JoinGameError.GameAlreadyStarted

            gameState is GameState.Waiting && gameState.players.size > gameConfig.maxPlayers -> JoinGameError.GameHasMaxPlayers(
                gameConfig.maxPlayers
            )

            gameState is GameState.Waiting
                    && gameState.players.any { it.userName.lowercase() == userName.lowercase() } -> JoinGameError.UsernameTaken

            else -> null
        }
    }

    private suspend fun joinGame(
        accessCode: String, userName: String, game: Game, id: String
    ): Try<Game> = tryWithTimeout(10.seconds) {
        gameRepository.join(
            accessCode = accessCode, userName = userName, userId = id
        ).fold(
            onSuccess = { game.success() },
            onFailure = { it.toJoinGameFailure() }
        )
    }

    private fun Throwable.toJoinGameFailure() = when (this) {
        is GameError.IncompatibleVersion -> JoinGameError.IncompatibleVersion(isCurrentLower = isCurrentLower)
            .failure()

        is GameError.GameNotFound -> JoinGameError.GameNotFound.failure()
        else -> JoinGameError.UnknownError(this).failure()
    }

    private suspend fun updateSession(userId: String, accessCode: String) {
        updateActiveGame(
            ActiveGame(
                accessCode = accessCode,
                userId = userId,
                isSingleDevice = false
            )
        )
    }

    private suspend fun checkForExistingSession() {
        if (session.activeGame != null) {
            clearActiveGame()
            developerSnackIfDebug { "User is already in an existing game while joining a game." }
        }
    }

    sealed class JoinGameError : Throwable() {
        data class InvalidAccessCodeLength(val requiredLength: Int) : JoinGameError()
        data class InvalidNameLength(val min: Int, val max: Int) : JoinGameError()
        data object GameNotFound : JoinGameError()
        data object GameAlreadyStarted : JoinGameError()
        data class IncompatibleVersion(val isCurrentLower: Boolean) : JoinGameError()
        data class GameHasMaxPlayers(val max: Int) : JoinGameError()
        data object UsernameTaken : JoinGameError()
        data class UnknownError(val t: Throwable) : JoinGameError()
    }
}

