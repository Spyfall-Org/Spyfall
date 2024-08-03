package com.dangerfield.libraries.game.internal

import android.util.Log
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.Player
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import oddoneout.core.Catching
import oddoneout.core.debugSnackOnError
import oddoneout.core.failure
import oddoneout.core.illegalStateFailure
import oddoneout.core.logOnFailure
import oddoneout.core.success
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Named

@AutoBind
@Named(MultiDeviceRepositoryName)
class MutliDeviceGameRepository @Inject constructor(
    private val backendGameDataSource: BackendGameDataSource,
    private val getGameState: GetGameState,
    private val gameConfig: GameConfig,
    private val mapBackendGameToDomainGame: MapBackendGameToDomainGame,
) : GameRepository {

    private val currentGameAccessCodeState = MutableStateFlow<String?>(null)

    private val currentAccessCodeGameFlow: Flow<GamePresence> = currentGameAccessCodeState
        .filterNotNull()
        .flatMapLatest { accessCode ->
            backendGameDataSource.subscribeToGame(accessCode)
                .filter { it.isSuccess || it.exceptionOrNull() is GameError.GameNotFound }
                .map { result ->
                    val backendGame = result.getOrNull()
                    val domainGame = backendGame?.let {
                        mapBackendGameToDomainGame(it).logOnFailure().getOrNull()
                    }
                    domainGame
                }
                .map { game ->
                    if (game != null) {
                        GamePresence.Present(game)
                    } else {
                        GamePresence.Absent(accessCode)
                    }
                }
        }

    /**
     * Returns a flow of the game with the provided access code
     * Null if game does not exist.
     */
    override fun getGameFlow(accessCode: String): Flow<Game?> {
        if (currentGameAccessCodeState.value != accessCode) {
            currentGameAccessCodeState.value = accessCode
        }

        return currentAccessCodeGameFlow
            .filterNotNull()
            .filter {
                when (it) {
                    is GamePresence.Present -> accessCode == it.game.accessCode
                    is GamePresence.Absent -> accessCode == it.accessCode
                }
            }
            .map { it.gameOrNull() }
            .flatMapLatest { gameWithUpdatingState(it) }
    }

    private fun gameWithUpdatingState(game: Game?) = if (game == null) {
        flow<Game?> { emit(null) }
    } else {
        val startedState = game.state as? Game.State.Started
        if (startedState != null) {
            elapsedSecondsFlow(startedState)
                .map { elapsedSeconds ->
                    val updatedState = getGameState(
                        elapsedSeconds = elapsedSeconds,
                        startedAt = game.startedAt,
                        timeLimitSeconds = game.timeLimitSeconds,
                        isBeingStarted = game.isBeingStarted,
                        lastActiveAt = game.lastActiveAt,
                        hasEveryoneVoted = game.players.all { it.votedCorrectly != null }
                    )

                    Log.d("GameRepository", "Updated state: $updatedState")

                    game.copy(state = updatedState)
                }
        } else {
            flow { emit(game) }
        }
    }

    private fun elapsedSecondsFlow(startedState: Game.State.Started): Flow<Int> {
        return flow {
            var elapsedSeconds = startedState.secondsElapsed
            while (currentCoroutineContext().isActive) {
                emit(elapsedSeconds)
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    override suspend fun create(game: Game): Catching<Unit> = Catching {
        backendGameDataSource.setGame(game)
    }

    override suspend fun join(
        accessCode: String,
        userId: String,
        userName: String
    ): Catching<Unit> =
        Catching {
            backendGameDataSource.addPlayer(
                accessCode, Player(
                    id = userId,
                    role = null,
                    userName = userName,
                    isOddOneOut = false,
                    isHost = false,
                    votedCorrectly = null
                )
            )
        }
            .logOnFailure()

    override suspend fun removeUser(accessCode: String, username: String): Catching<Unit> {
        val currentGame = getGameFlow(accessCode).first()

        return when {
            currentGame == null || currentGame.accessCode != accessCode -> {
                // assume best intent, try to remove from the provided access code anyway
                backendGameDataSource.removePlayer(accessCode, username)
            }

            currentGame.isBeingStarted -> failure(GameError.TriedToLeaveStartedGame())
            currentGame.players.size == 1 -> backendGameDataSource.delete(accessCode)
            else -> backendGameDataSource.removePlayer(accessCode, username)
        }.logOnFailure()
    }

    override suspend fun doesGameExist(accessCode: String): Catching<Boolean> {
        return backendGameDataSource.getGame(accessCode).fold(
            onSuccess = { true.success() },
            onFailure = {
                if (it is GameError.GameNotFound) {
                    false.success()
                } else {
                    failure(it)
                }
            }
        )
            .logOnFailure()
    }

    override suspend fun assignHost(accessCode: String, id: String): Catching<Unit> {
        return backendGameDataSource.setHost(accessCode = accessCode, id = id)
            .logOnFailure()
    }

    override suspend fun end(accessCode: String) {
        backendGameDataSource.delete(accessCode)
    }

    override suspend fun setGameIsBeingStarted(
        accessCode: String,
        isBeingStarted: Boolean
    ): Catching<Unit> {

        val currentGame = getGameFlow(accessCode).first()
            ?: return illegalStateFailure { "Game is null when setting starting" }

        return if (currentGame.isBeingStarted == isBeingStarted) {
            failure(GameError.GameAlreadyStartedError())
        } else {
            backendGameDataSource.setGameBeingStarted(accessCode, isBeingStarted)
        }
            .logOnFailure()
    }

    override suspend fun start(accessCode: String): Catching<Unit> {
        return backendGameDataSource.setStartedAt(accessCode).logOnFailure()
    }

    override suspend fun reset(accessCode: String): Catching<Unit> = Catching {
        val currentGame = getGameFlow(accessCode).first()
            ?: return illegalStateFailure { "Game is null when resetting" }

        val allItems =
            currentGame.packs.map { it.items }.flatten().filter { it != currentGame.secretItem }

        val newSecretItem = allItems.random()

        val resetGame = currentGame.copy(
            secretItem = newSecretItem,
            isBeingStarted = false,
            players = currentGame.players.map {
                it.copy(
                    role = null,
                    isOddOneOut = false,
                    votedCorrectly = null
                )
            },
            secretOptions = allItems.map { it.name }.shuffled().take(gameConfig.itemsPerGame),
            startedAt = null,
        )

        backendGameDataSource.setGame(resetGame)
    }
        .logOnFailure()
        .debugSnackOnError { "Could not reset game" }

    override suspend fun changeName(
        accessCode: String,
        newName: String,
        id: String
    ): Catching<Unit> {
        return backendGameDataSource.changeName(accessCode, newName, id)
            .logOnFailure()
    }

    override suspend fun updatePlayers(accessCode: String, players: List<Player>): Catching<Unit> {
        return backendGameDataSource.updatePlayers(accessCode, players).logOnFailure()
    }

    override suspend fun getGame(accessCode: String): Catching<Game> =
        Catching { getGameFlow(accessCode).first()!! }

    override suspend fun submitVoteForSecret(
        accessCode: String,
        voterId: String,
        secret: String
    ): Catching<Unit> {

        val currentGame = getGameFlow(accessCode).first()
            ?: return illegalStateFailure { "Game is null when submitting vote" }

        return backendGameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = secret == currentGame.secretItem.name
        ).logOnFailure()
    }

    @Suppress("ReturnCount")
    override suspend fun submitVoteForOddOneOut(
        accessCode: String,
        voterId: String,
        voteId: String
    ): Catching<Boolean> {

        val currentGame = getGameFlow(accessCode).first()
            ?: return illegalStateFailure { "Game is null when submitting vote" }

        val oddOneOut = currentGame
            .players
            .find { it.isOddOneOut }
            ?: return illegalStateFailure { "Could not pull Odd One Out From Game" }

        return backendGameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = voteId == oddOneOut.id
        )
            .logOnFailure()
            .map { voteId == oddOneOut.id }
    }

    companion object {
        const val name = "MultiDeviceGameRepository"
    }
}

sealed class GamePresence {
    data class Present(val game: Game) : GamePresence()
    data class Absent(val accessCode: String) : GamePresence()

    fun gameOrNull(): Game? = when (this) {
        is Present -> game
        else -> null
    }
}