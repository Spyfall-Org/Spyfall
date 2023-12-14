package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameError.TriedToLeaveStartedGame
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.LocationPackRepository
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
import spyfallx.core.developerSnackOnError
import spyfallx.core.failure
import spyfallx.core.illegalState
import spyfallx.core.logOnError
import spyfallx.core.success
import java.time.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AutoBind
class GameRepositoryImpl @Inject constructor(
    private val gameDataSource: GameDataSource,
    private val clock: Clock,
    private val getGamePlayLocations: GetGamePlayLocations,
    private val locationPackRepository: LocationPackRepository,
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
            started = SharingStarted.WhileSubscribed(GameSubscriptionTimeout),
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

    override suspend fun removeUser(accessCode: String, username: String): Try<Unit> {
        val currentGame = currentGameFlow.value.takeIf { it?.accessCode == accessCode }
            ?: gameDataSource.getGame(accessCode).getOrNull()

        return when {
            currentGame == null || currentGame.accessCode != accessCode -> {
                // assume best intent, try to remove from the provided access code anyway
                gameDataSource.removePlayer(accessCode, username)
            }

            currentGame.isBeingStarted -> TriedToLeaveStartedGame.failure()
            currentGame.players.size == 1 -> gameDataSource.delete(accessCode)
            else -> gameDataSource.removePlayer(accessCode, username)
        }.logOnError()
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
        gameDataSource.delete(accessCode)
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

    override suspend fun reset(accessCode: String): Try<Unit> {
        val currentGame = currentGameFlow.value.takeIf { it?.accessCode == accessCode }
            ?: gameDataSource.getGame(accessCode).getOrNull()
            ?: return illegalState("Game is null when resetting")

        val packs = locationPackRepository
            .getPacks()
            .getOrThrow()
            .filter { it.name in currentGame.packNames }

        val newLocations = getGamePlayLocations(packs)
            .getOrNull()
            ?.map { it.name }
            ?: currentGame.locationOptionNames

        var newLocation = newLocations.random()

        while (newLocation == currentGame.locationName) {
            newLocation = newLocations.random()
        }

        val resetGame = currentGame.copy(
            locationName = newLocation,
            isBeingStarted = false,
            players = currentGame.players.map {
                it.copy(
                    role = null,
                    isOddOneOut = false,
                    votedCorrectly = null
                )
            },
            locationOptionNames = newLocations,
            startedAt = null,
        )
        return Try {
            gameDataSource.setGame(resetGame)
        }
            .logOnError()
            .developerSnackOnError { "Could not reset game" }
    }

    override suspend fun changeName(accessCode: String, newName: String, id: String): Try<Unit> {
        return gameDataSource.changeName(accessCode, newName, id)
            .logOnError()
    }

    override suspend fun updatePlayers(accessCode: String, players: List<Player>): Try<Unit> {
        return gameDataSource.updatePlayers(accessCode, players).logOnError()
    }

    override suspend fun getGame(accessCode: String): Try<Game> = gameDataSource.getGame(accessCode)

    override suspend fun submitLocationVote(
        accessCode: String,
        voterId: String,
        location: String
    ): Try<Unit> {
        val game = currentGameFlow.value ?: return illegalState("Game is null when voting")
        return gameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = location == game.locationName
        ).logOnError()
    }

    override suspend fun submitOddOneOutVote(
        accessCode: String,
        voterId: String,
        voteId: String
    ): Try<Unit> {
        val oddOneOut = currentGameFlow.value
            ?.players
            ?.find { it.isOddOneOut }
            ?: return illegalState("Could not pull Odd One Out From Game")

        return gameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = voteId == oddOneOut.id
        )
            .logOnError()
    }

    companion object {
        private const val GameSubscriptionTimeout = 5_000L
    }
}