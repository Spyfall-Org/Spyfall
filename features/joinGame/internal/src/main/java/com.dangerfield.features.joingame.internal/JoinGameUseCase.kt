package com.dangerfield.features.joingame.internal

import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.GenerateLocalUUID
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
    private val clearActiveGame: ClearActiveGame,
    private val generateLocalUUID: GenerateLocalUUID
) {
    suspend operator fun invoke(
        accessCode: String, userName: String
    ): Try<Game> {
        checkForExistingSession()
        val userId = session.user.id ?: generateLocalUUID()

        return when {
            accessCode.length != gameConfig.accessCodeLength ->
                JoinGameError.InvalidAccessCodeLength(gameConfig.accessCodeLength).failure()

            userName.length !in gameConfig.minNameLength..gameConfig.maxNameLength ->
                JoinGameError.InvalidNameLength(gameConfig.minNameLength, gameConfig.maxNameLength)
                    .failure()

            else -> joinGame(
                accessCode = accessCode,
                userName = userName,
                userId = userId,
            )
        }
    }

    private suspend fun joinGame(
        accessCode: String, userName: String, userId: String
    ): Try<Game> {
        val game = gameRepository.getGame(accessCode).getOrNull()
            ?: return JoinGameError.GameNotFound.failure()

        val joinError = getGameStateError(accessCode, game, userName)

        return joinError?.failure()
            ?: tryWithTimeout(5.seconds) {
                gameRepository.join(
                    accessCode = accessCode,
                    userName = userName,
                    userId = userId
                )
            }
                .onSuccess { updateSession(userId, accessCode) }
                .map { game }
                .mapFailure { it.toJoinGameError() }
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

            gameState is GameState.Waiting && gameState.players.size >= gameConfig.maxPlayers -> JoinGameError.GameHasMaxPlayers(
                gameConfig.maxPlayers
            )

            gameState is GameState.Waiting
                    && gameState.players.any { it.userName.lowercase() == userName.lowercase() } -> JoinGameError.UsernameTaken

            else -> null
        }
    }

    private fun Throwable.toJoinGameError(): Throwable = when (this) {
        is JoinGameError -> this
        is GameError.IncompatibleVersion -> JoinGameError.IncompatibleVersion(isCurrentLower = isCurrentLower)
        is GameError.GameNotFound -> JoinGameError.GameNotFound
        else -> JoinGameError.UnknownError(this)
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

