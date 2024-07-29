package com.dangerfield.features.joingame.internal

import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameDataSourcError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.game.PackResult
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateActiveGame
import oddoneout.core.GenerateLocalUUID
import oddoneout.core.Catching
import oddoneout.core.showDebugSnack
import oddoneout.core.failure
import oddoneout.core.mapFailure
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

class JoinGameUseCase @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    private val gameConfig: GameConfig,
    private val session: Session,
    private val packRepository: PackRepository,
    private val updateActiveGame: UpdateActiveGame,
    private val clearActiveGame: ClearActiveGame,
    private val generateLocalUUID: GenerateLocalUUID
) {
    suspend operator fun invoke(
        accessCode: String, userName: String
    ): Catching<Game> {
        checkForExistingSession()
        val userId = session.user.id ?: generateLocalUUID()

        return when {
            accessCode.length != gameConfig.accessCodeLength ->
                failure(JoinGameError.InvalidAccessCodeLength(gameConfig.accessCodeLength))

            userName.length !in gameConfig.minNameLength..gameConfig.maxNameLength ->
                failure(JoinGameError.InvalidNameLength(gameConfig.minNameLength, gameConfig.maxNameLength))

            else -> joinGame(
                accessCode = accessCode,
                userName = userName,
                userId = userId,
            )
        }
    }

    private suspend fun joinGame(
        accessCode: String, userName: String, userId: String
    ): Catching<Game> {
        // TODO I think this is a problem
        val game = gameRepository.getGame(accessCode).getOrElse {
            return failure(it.toJoinGameError())
        }

        val joinError = getGameStateError(accessCode, game, userName)

        return joinError?.let { failure(it) }
            ?: tryWithTimeout(5.seconds) {
                gameRepository.join(
                    accessCode = accessCode,
                    userName = userName,
                    userId = userId
                )
            }
                .onSuccess { updateSession(userId, accessCode) }
                .mapCatching { game }
                .mapFailure { it.toJoinGameError() }
    }

    private suspend fun getGameStateError(
        accessCode: String,
        game: Game,
        userName: String
    ): JoinGameError? {
        val gameState = mapToGameState(accessCode, game)

        return when(gameState) {
            is GameState.DoesNotExist -> JoinGameError.GameNotFound
            is GameState.Voting,
            is GameState.VotingEnded,
            is GameState.Started,
            is GameState.Starting -> JoinGameError.GameAlreadyStarted
            is GameState.Waiting -> {

                if (gameState.players.size >= gameConfig.maxPlayers) {
                    JoinGameError.GameHasMaxPlayers(gameConfig.maxPlayers)
                } else if (gameState.players.any { it.userName.lowercase() == userName.lowercase() }) {
                    JoinGameError.UsernameTaken
                } else {
                    null
                }
            }
            is GameState.Expired,
            is GameState.Unknown -> null
        }
    }

    private fun Throwable.toJoinGameError(): Throwable = when (this) {
        is JoinGameError -> this
        is GameDataSourcError.IncompatibleVersion -> JoinGameError.IncompatibleVersion(isCurrentLower = isCurrentLower)
        is GameDataSourcError.GameNotFound -> JoinGameError.GameNotFound
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
            showDebugSnack { "User is already in an existing game while joining a game." }
        }
    }

    sealed class JoinGameError : Error() {
        data class InvalidAccessCodeLength(val requiredLength: Int) : JoinGameError()
        data class InvalidNameLength(val min: Int, val max: Int) : JoinGameError()
        data object GameNotFound : JoinGameError()
        data object GameAlreadyStarted : JoinGameError()
        data class IncompatibleVersion(val isCurrentLower: Boolean) : JoinGameError()
        data object CouldNotFetchPacksNeeded : JoinGameError()
        data class GameHasMaxPlayers(val max: Int) : JoinGameError()
        data object UsernameTaken : JoinGameError()
        data class UnknownError(val t: Throwable) : JoinGameError()
    }
}

