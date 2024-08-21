package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.session.Session
import com.dangerfield.oddoneoout.libraries.game.internal.R
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import oddoneout.core.Catching
import oddoneout.core.debugSnackOnError
import oddoneout.core.doNothing
import oddoneout.core.ignoreValue
import oddoneout.core.illegalStateFailure
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug
import se.ansman.dagger.auto.AutoBind
import java.time.Clock
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@AutoBind
@Singleton
@Named(SingleDeviceRepositoryName)
class SingleDeviceGameRepository
@Inject constructor(
    private val clock: Clock,
    private val gameConfig: GameConfig,
    private val dictionary: Dictionary,
    private val session: Session,
    private val getGameState: GetGameState,
) : GameRepository {

    private val gameUpdates = MutableStateFlow<Game?>(null)

    private val gameWithState = gameUpdates
        .map { game ->
            fun elapsedSeconds(startedAt: Long?): Int {
                val startedAtMillis = startedAt ?: return 0
                val elapsedMillis: Long = clock.millis() - startedAtMillis
                return (elapsedMillis / 1000).toInt()
            }

            if (game != null) {
                val updatedState = getGameState(
                    elapsedSeconds = elapsedSeconds(game.startedAt),
                    startedAt = game.startedAt,
                    timeLimitSeconds = game.timeLimitSeconds,
                    isBeingStarted = game.isBeingStarted,
                    lastActiveAt = game.lastActiveAt,
                    hasEveryoneVoted = game.players.all { it.votedCorrectly != null }
                )

                game.copy(state = updatedState)
            } else {
                null
            }
        }

    override suspend fun create(game: Game): Catching<Unit> = Catching {
        gameUpdates.value = game
    }
        .throwIfDebug()
        .ignoreValue()

    override suspend fun join(
        accessCode: String,
        userId: String,
        userName: String
    ): Catching<Unit> =
        Catching {
            // single device games cannot be joined
            doNothing()
        }

    override suspend fun removeUser(accessCode: String, username: String): Catching<Unit> =
        Catching {
            // single device games cannot remove players, theres no waiting screen
            doNothing()
        }

    override suspend fun assignHost(accessCode: String, id: String): Catching<Unit> {
        return Catching {
            // single device games dont have hosts or leaving
            doNothing()
        }
    }

    override suspend fun doesGameExist(accessCode: String): Catching<Boolean> = Catching {
        getGame(accessCode).getOrNull()?.accessCode == accessCode
    }

    override suspend fun end(accessCode: String) {
        gameUpdates.value = null
    }

    override suspend fun start(accessCode: String): Catching<Unit> = Catching {
        updateGame {
            it.copy(startedAt = clock.millis())
        }
    }
        .throwIfDebug()
        .ignoreValue()

    override suspend fun setGameIsBeingStarted(
        accessCode: String,
        isBeingStarted: Boolean
    ): Catching<Unit> = Catching {
        updateGame {
            it.copy(isBeingStarted = isBeingStarted)
        }
    }

    override suspend fun reset(accessCode: String): Catching<Unit> = Catching {
        val currentGame = getGame(accessCode).getOrNull()
            ?: return illegalStateFailure { "Single Device Game is null when resetting" }

        val optionsForSecret = currentGame
            .packs
            .map { it.packItems }
            .flatten()
            .filter { it != currentGame.secretItem }
            .take(gameConfig.itemsPerSingleDeviceGame)

        val secretItem = optionsForSecret.random()

        val shuffledPlayerWithRoles = getUpdatedPlayers(secretItem.roles, currentGame.players)

        val game = Game(
            secretItem = secretItem,
            isBeingStarted = false,
            players = shuffledPlayerWithRoles,
            timeLimitSeconds = currentGame.timeLimitSeconds,
            startedAt = null,
            secretOptions = optionsForSecret.map { it.name },
            videoCallLink = null,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis(),
            packsVersion = gameConfig.packsVersion,
            languageCode = session.user.languageCode,
            packs = currentGame.packs,
            state = Game.State.Waiting,
            mePlayer = currentGame.mePlayer
        )

        return updateGame { game }
            .ignoreValue()
            .logOnFailure()
            .debugSnackOnError { "Could not reset game" }
    }
        .logOnFailure("Could not reset game")
        .throwIfDebug()

    private fun getUpdatedPlayers(
        roles: List<String>?,
        players: List<Player>
    ): List<Player> {
        val shuffledRolesQueue: LinkedList<String>? = roles?.shuffled()?.let { LinkedList(it) }
        val defaultRole = roles?.randomOrNull()

        val shuffledPlayers = players.shuffled()
        val oddOneOutIndex = shuffledPlayers.indices.random()

        val shuffledPlayersWithRoles = shuffledPlayers.mapIndexed { index, player ->
            val role = if (index == oddOneOutIndex) {
                dictionary.getString(R.string.app_theOddOneOutRole_text)
            } else {
                shuffledRolesQueue?.poll() ?: defaultRole
            }

            player.copy(
                role = role,
                isOddOneOut = index == oddOneOutIndex,
                votedCorrectly = null
            )
        }
        return shuffledPlayersWithRoles
    }

    override suspend fun changeName(
        accessCode: String,
        newName: String,
        id: String
    ): Catching<Unit> =
        Catching {
            updateGame {
                it.copy(
                    players = it.players.map { player ->
                        if (player.id == id) {
                            player.copy(userName = newName)
                        } else {
                            player
                        }
                    }
                )
            }
        }

    override suspend fun updatePlayers(accessCode: String, players: List<Player>): Catching<Unit> {
        return updateGame {
            it.copy(players = players)
        }.ignoreValue()
    }

    override fun getGameFlow(accessCode: String): Flow<Game?> =
        gameWithState.flatMapLatest { gameWithUpdatingState(it) }

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

    override suspend fun getGame(accessCode: String): Catching<Game> = Catching {
        getGameFlow(accessCode)
            .filterNotNull()
            .first()
    }

    override suspend fun submitVoteForSecret(
        accessCode: String,
        voterId: String,
        secret: String
    ): Catching<Unit> = Catching {
        val game = getGame(accessCode).getOrNull()
            ?: return illegalStateFailure { "Game is null when voting" }
        updateGame {
            it.copy(players = it.players.map { player ->
                if (player.id == voterId) {
                    player.copy(votedCorrectly = secret == game.secretItem.name)
                } else {
                    player
                }
            })
        }
            .logOnFailure()
    }

    override suspend fun submitVoteForOddOneOut(
        accessCode: String,
        voterId: String,
        voteId: String
    ): Catching<Boolean> = updateGame {
        val oddOneOutId = it.players.find { p -> p.isOddOneOut }?.id
            ?: throw IllegalStateException("No odd one out")

        it.copy(players = it.players.map { player ->
            if (player.id == voterId) {
                player.copy(votedCorrectly = voteId == oddOneOutId)
            } else {
                player
            }
        })
    }.map { game ->
        game.players.find { it.id == voterId }?.votedCorrectly ?: false
    }


    private fun Game.withUpdatedLastActiveAt() = copy(lastActiveAt = clock.millis())

    private suspend fun updateGame(update: (Game) -> Game): Catching<Game> {
        val currentGame = gameUpdates.filterNotNull().firstOrNull()?.withUpdatedLastActiveAt()
            ?: return illegalStateFailure { "Game not in state" }

        return Catching {
            val updatedGame = update(currentGame)
            gameUpdates.value = updatedGame
            updatedGame
        }
            .throwIfDebug()
            .logOnFailure("Could not update the single device game")
    }


    companion object {
        const val name = "SingleDeviceRepository"
    }
}