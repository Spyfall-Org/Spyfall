package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.StartGameError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import spyfallx.core.allOrNone
import spyfallx.core.failure
import spyfallx.core.illegalState
import spyfallx.core.logOnError
import spyfallx.core.success
import java.time.Clock
import javax.inject.Inject

@AutoBind
class GameRepositoryImpl @Inject constructor(
    private val gameDataSource: GameDataSource,
    private val clock: Clock,
    @ApplicationScope private val applicationScope: CoroutineScope
) : GameRepository {

    private val currentGameAccessCodeState = MutableStateFlow<String?>(null)

    private val currentGameFlow: StateFlow<Game?> = currentGameAccessCodeState
        .filterNotNull()
        .flatMapLatest {
            gameDataSource.subscribeToGame(it).getOrNull() ?: flowOf(null)
        }
        .filterNotNull()
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override fun getGameFlow(accessCode: String): Flow<Game> {
        if (currentGameAccessCodeState.value != accessCode) {
            currentGameAccessCodeState.value = accessCode
        }
        return currentGameFlow.filterNotNull()
    }

    override suspend fun create(game: Game): Try<Unit> = Try {
        gameDataSource.setGame(game)
    }

    override suspend fun join(accessCode: String, userId: String, userName: String): Try<Unit> =
        Try {
            gameDataSource.addPlayer(
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
            .logOnError()

    override suspend fun removeUser(accessCode: String, username: String) {
        TODO("Not yet implemented")
    }

    override suspend fun doesGameExist(accessCode: String): Try<Boolean> {
        return gameDataSource.getGame(accessCode).fold(
            onSuccess = { true.success() },
            onFailure = {
                if (it is GameError.GameNotFound) {
                    false.success()
                } else {
                    it.failure()
                }
            }
        )
            .logOnError()
    }

    override suspend fun end(accessCode: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setGameIsBeingStarted(
        accessCode: String,
        isBeingStarted: Boolean
    ): Try<Unit> =
        if (gameDataSource.getGame(accessCode).getOrThrow().isBeingStarted == isBeingStarted) {
            StartGameError.GameAlreadyStarted.failure()
        } else {
            gameDataSource.setGameBeingStarted(accessCode, isBeingStarted)
        }
            .logOnError()

    override suspend fun start(accessCode: String): Try<Unit> {
        return gameDataSource.setStartedAt(accessCode, clock.millis()).logOnError()
    }

    override suspend fun reset(accessCode: String) {
        TODO("Not yet implemented")
    }

    override suspend fun changeName(accessCode: String, newName: String, id: String): Try<Unit> {
        return gameDataSource.changeName(accessCode, newName, id)
            .logOnError()
    }

    override suspend fun updatePlayers(accessCode: String, players: List<Player>): Try<Unit> {
        return gameDataSource.updatePlayers(accessCode, players).logOnError()
    }

    override suspend fun getGame(accessCode: String): Try<Game> = gameDataSource.getGame(accessCode)

    override suspend fun submitLocationVote(accessCode: String, voterId: String, location: String): Try<Unit> {
        val game = currentGameFlow.value ?: return illegalState("Game is null when voting")
        return gameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = location == game.locationName).logOnError()
    }

    override suspend fun submitOddOneOutVote(accessCode: String, voterId: String, voteId: String): Try<Unit> {
        val oddOneOut = currentGameFlow.value
            ?.players
            ?.find { it.isOddOneOut }
            ?: return illegalState("Could not pull Odd One Out From Game")

        return gameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = voteId == oddOneOut.id)
            .logOnError()
    }
}