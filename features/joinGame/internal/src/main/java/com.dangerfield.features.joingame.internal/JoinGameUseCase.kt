package com.dangerfield.features.joingame.internal

import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
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
    private val gameConfig: GameConfig,
    private val session: Session,
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
        val game = gameRepository.getGame(accessCode).getOrElse {
            return failure(it.toJoinGameError())
        }

        val joinError = getGameStateError(game, userName)

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
        game: Game,
        userName: String
    ): JoinGameError? {

        return when(game.state) {
            Game.State.Voting,
            Game.State.Results,
            is Game.State.Started,
            Game.State.Starting -> JoinGameError.GameAlreadyStarted()
            Game.State.Waiting -> {

                if (game.players.size >= gameConfig.maxPlayers) {
                    JoinGameError.GameHasMaxPlayers(gameConfig.maxPlayers)
                } else if (game.players.any { it.userName.lowercase() == userName.lowercase() }) {
                    JoinGameError.UsernameTaken()
                } else {
                    null
                }
            }
            Game.State.Expired,
            Game.State.Unknown -> null
        }
    }

    private fun Throwable.toJoinGameError(): Throwable = when (this) {
        is JoinGameError -> this
        is GameError.IncompatibleVersion -> JoinGameError.IncompatibleVersion(isCurrentLower = isCurrentLower)
        is GameError.GameNotFound -> JoinGameError.GameNotFound()
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
        class InvalidAccessCodeLength(val requiredLength: Int) : JoinGameError()
        class InvalidNameLength(val min: Int, val max: Int) : JoinGameError()
        class GameNotFound : JoinGameError()
        class GameAlreadyStarted : JoinGameError()
        class IncompatibleVersion(val isCurrentLower: Boolean) : JoinGameError()
        class CouldNotFetchPacksNeeded : JoinGameError()
        class GameHasMaxPlayers(val max: Int) : JoinGameError()
        class UsernameTaken : JoinGameError()
        class UnknownError(val t: Throwable) : JoinGameError()
    }
}

