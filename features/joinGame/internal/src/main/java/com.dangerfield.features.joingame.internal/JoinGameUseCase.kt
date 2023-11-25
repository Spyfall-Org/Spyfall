package com.dangerfield.features.joingame.internal

import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.session.SessionState
import com.dangerfield.libraries.session.SessionStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import spyfallx.core.Try
import spyfallx.core.failure
import spyfallx.core.success
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class JoinGameUseCase @Inject constructor(
    private val sessionStateRepository: SessionStateRepository,
    private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val gameConfig: GameConfig
) {
    suspend operator fun invoke(
        accessCode: String, userName: String
    ): Try<Game> {

        checkForExistingSession()

        val id = UUID.randomUUID().toString()

        if (accessCode.length < gameConfig.accessCodeLength || accessCode.length > gameConfig.accessCodeLength) {
            return JoinGameError.InvalidAccessCodeLength(requiredLength = gameConfig.accessCodeLength)
                .failure()
        }

        if (userName.length > gameConfig.maxNameLength || userName.length < gameConfig.minNameLength) {
            return JoinGameError.InvalidNameLength(
                min = gameConfig.minNameLength, max = gameConfig.maxNameLength
            ).failure()
        }

        return gameRepository.getGame(accessCode).fold(ifSuccess = { game ->
            val joinGameError = getGameStateError(accessCode, game, userName)

            joinGameError?.failure() ?: joinGame(
                accessCode = accessCode, userName = userName, game = game, id = id
            )
        }, ifFailure = {
            it.toJoinGameFailure()
        }).onSuccess {
            updateSession(userName, accessCode)
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
            gameState is GameState.TimedOut -> JoinGameError.GameAlreadyStarted

            gameState is GameState.Waiting && gameState.players.size > gameConfig.maxPlayers -> JoinGameError.GameHasMaxPlayers(
                gameConfig.maxPlayers
            )

            gameState is GameState.Waiting && gameState.players.any { it.userName == userName } -> JoinGameError.UsernameTaken

            else -> null
        }
    }

    private suspend fun joinGame(
        accessCode: String, userName: String, game: Game, id: String
    ): Try<Game> = tryWithTimeout(10.seconds) {
        gameRepository.join(
            accessCode = accessCode, userName = userName, id = id
        ).fold(
            ifSuccess = { game.success() },
            ifFailure = { it.toJoinGameFailure() }
        )
    }

    private fun Throwable.toJoinGameFailure() = when (this) {
        is GameError.IncompatibleVersion -> JoinGameError.IncompatibleVersion(isCurrentLower = isCurrentLower)
            .failure()

        is GameError.GameNotFound -> JoinGameError.GameNotFound.failure()
        else -> JoinGameError.UnknownError(this).failure()
    }

    private fun updateSession(name: String, accessCode: String) {
        sessionStateRepository.updateSessionState(
            SessionState.InGame(
                username = name, accessCode = accessCode
            )
        )
    }

    private fun checkForExistingSession() {
        applicationScope.launch {
            sessionStateRepository.getSessionState().let { sessionState ->
                if (sessionState is SessionState.InGame) {
                    Timber.d("User is already in an existing game while joining a game. ")
                }
            }
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

