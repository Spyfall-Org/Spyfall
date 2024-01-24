package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameDataSourcError
import com.dangerfield.libraries.game.GameDataSourcError.TriedToLeaveStartedGameDataSourc
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.StartGameError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.Try
import oddoneout.core.developerSnackOnError
import oddoneout.core.failure
import oddoneout.core.illegalState
import oddoneout.core.logOnError
import oddoneout.core.success
import java.time.Clock
import javax.inject.Inject
import javax.inject.Named

@Named(MultiDeviceRepositoryName)
@AutoBind
class MutliDeviceGameRepository @Inject constructor(
    private val gameDataSource: GameDataSource,
    private val getGamePlayLocations: GetGamePlayLocations,
    private val locationPackRepository: LocationPackRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : GameRepository {

    private val currentGameAccessCodeState = MutableStateFlow<String?>(null)

    sealed class GamePresence {
        data class Present(val game: Game) : GamePresence()
        data class Absent(val accessCode: String) : GamePresence()

        fun gameOrNull(): Game? = when (this) {
            is Present -> game
            else -> null
        }
    }

    /**
     * Flow based on the current access code that subscribes to the game with that access code
     * and emits the game wrapped in its presence.
     */
    private val currentGameFlow: StateFlow<GamePresence?> =
        currentGameAccessCodeState
            .filterNotNull()
            .flatMapLatest { accessCode ->
                gameDataSource.subscribeToGame(accessCode)
                    .filter {
                        // if there is an error parsing ignore it.
                        it.isSuccess || it.getExceptionOrNull() is GameDataSourcError.GameNotFound
                    }
                    .map {
                        val game = it.getOrNull()

                        if (game != null) {
                            GamePresence.Present(game)
                        } else {
                            GamePresence.Absent(accessCode)
                        }
                    }
            }
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    /**
     * Returns a flow of the game with the provided access code
     * Null if game does not exist.
     */
    override fun getGameFlow(accessCode: String): Flow<Game?> {
        if (currentGameAccessCodeState.value != accessCode) {
            currentGameAccessCodeState.value = accessCode
        }

        // wait for values relevant to the access code.
        return currentGameFlow
            .filterNotNull()
            .filter { gamePresence ->
                when (gamePresence) {
                    is GamePresence.Present -> gamePresence.game.accessCode == accessCode
                    is GamePresence.Absent -> gamePresence.accessCode == accessCode
                }
            }
            .map { it.gameOrNull() }
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
        val currentGame = getGameFlow(accessCode).first() ?: gameDataSource.getGame(accessCode).getOrNull()

        return when {
            currentGame == null || currentGame.accessCode != accessCode -> {
                // assume best intent, try to remove from the provided access code anyway
                gameDataSource.removePlayer(accessCode, username)
            }

            currentGame.isBeingStarted -> TriedToLeaveStartedGameDataSourc.failure()
            currentGame.players.size == 1 -> gameDataSource.delete(accessCode)
            else -> gameDataSource.removePlayer(accessCode, username)
        }.logOnError()
    }

    override suspend fun doesGameExist(accessCode: String): Try<Boolean> {
        return gameDataSource.getGame(accessCode).fold(
            onSuccess = { true.success() },
            onFailure = {
                if (it is GameDataSourcError.GameNotFound) {
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
    ): Try<Unit> {

        val currentGame = getGameFlow(accessCode).first()
            ?: gameDataSource.getGame(accessCode).getOrNull()
            ?: return illegalState("Game is null when setting starting")

        return if (currentGame.isBeingStarted == isBeingStarted) {
            StartGameError.GameDataSourcAlreadyStarted.failure()
        } else {
            gameDataSource.setGameBeingStarted(accessCode, isBeingStarted)
        }
            .logOnError()
    }

    override suspend fun start(accessCode: String): Try<Unit> {
        return gameDataSource.setStartedAt(accessCode).logOnError()
    }

    override suspend fun reset(accessCode: String): Try<Unit> {
        val currentGame = getGameFlow(accessCode).first()
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

        val currentGame = getGameFlow(accessCode).first()
            ?: gameDataSource.getGame(accessCode).getOrNull()
            ?: return illegalState("Game is null when submitting vote")

        return gameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = location == currentGame.locationName
        ).logOnError()
    }

    override suspend fun submitOddOneOutVote(
        accessCode: String,
        voterId: String,
        voteId: String
    ): Try<Boolean> {

        val currentGame = getGameFlow(accessCode).first()
            ?: gameDataSource.getGame(accessCode).getOrNull()
            ?: return illegalState("Game is null when submitting vote")

        val oddOneOut = currentGame
            .players
            .find { it.isOddOneOut }
            ?: return illegalState("Could not pull Odd One Out From Game")

        return gameDataSource.setPlayerVotedCorrectly(
            accessCode = accessCode,
            playerId = voterId,
            votedCorrectly = voteId == oddOneOut.id
        )
            .logOnError()
            .map { voteId == oddOneOut.id }
    }

    companion object {
        private const val GameSubscriptionTimeout = 5_000L
        const val name = "MultiDeviceGameRepository"
    }
}