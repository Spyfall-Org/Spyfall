package com.dangerfield.libraries.game.internal

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import spyfallx.core.developerSnackOnError
import spyfallx.core.doNothing
import spyfallx.core.illegalState
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug
import java.time.Clock
import javax.inject.Inject
import javax.inject.Named

@Named(SingleDeviceRepositoryName)
@AutoBind
class SingleDeviceGameRepository
@Inject constructor(
    private val datastore: DataStore<Preferences>,
    private val clock: Clock,
    private val locationPackRepository: LocationPackRepository,
    private val getGamePlayLocations: GetGamePlayLocations,
    @ApplicationScope private val applicationScope: CoroutineScope,
    moshi: Moshi,
) : GameRepository {
    private val jsonAdapter = moshi.adapter(Game::class.java)

    private val gameFlow = datastore.data.map {
        it[gamePreferenceKey]?.let { json ->
            Log.d("Elijah", "data store got non empty game")
            jsonAdapter.fromJson(json)
        } ?: run {
            Log.d("Elijah", "data store got null game")
            null
        }
    }
        .stateIn(
            applicationScope,
            SharingStarted.Eagerly,
            null
        )

    override suspend fun create(game: Game): Try<Unit> = Try {
        Log.d("Elijah", "Creating game")
        datastore.updateData {
            it.toMutablePreferences().apply {
                val gameJson = jsonAdapter.toJson(game)
                this[gamePreferenceKey] = gameJson
            }
        }
    }
        .throwIfDebug()
        .ignoreValue()

    override suspend fun join(accessCode: String, userId: String, userName: String): Try<Unit> =
        Try {
            // single device games cannot be joined
            doNothing()
        }

    override suspend fun removeUser(accessCode: String, username: String): Try<Unit> = Try {
        // single device games cannot remove players, theres no waiting screen
        doNothing()
    }

    override suspend fun doesGameExist(accessCode: String): Try<Boolean> = Try {
        gameFlow.value?.accessCode == accessCode
    }

    override suspend fun end(accessCode: String) {
        Try {
            // remove from DB
            datastore.updateData {
                it.toMutablePreferences().apply {
                    remove(gamePreferenceKey)
                }
            }
        }
    }

    override suspend fun start(accessCode: String): Try<Unit> = Try {
        updateGame {
            it.copy(startedAt = clock.millis())
        }
    }
        .throwIfDebug()
        .ignoreValue()

    override suspend fun setGameIsBeingStarted(
        accessCode: String,
        isBeingStarted: Boolean
    ): Try<Unit> = Try {
        updateGame {
            it.copy(isBeingStarted = isBeingStarted)
        }
    }

    override suspend fun reset(accessCode: String): Try<Unit> = Try {
        val currentGame = gameFlow.value.takeIf { it?.accessCode == accessCode }
            ?: return illegalState("Single Device Game is null when resetting")

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
        return updateGame {
            resetGame
        }
            .logOnError()
            .developerSnackOnError { "Could not reset game" }
    }

    override suspend fun changeName(accessCode: String, newName: String, id: String): Try<Unit> =
        Try {
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

    override suspend fun updatePlayers(accessCode: String, players: List<Player>): Try<Unit> {
        return updateGame {
            it.copy(players = players)
        }
    }

    override fun getGameFlow(accessCode: String): Flow<Game> =
        gameFlow
            .filterNotNull()
            .filter { it.accessCode == accessCode }

    override suspend fun getGame(accessCode: String): Try<Game> = Try {
        gameFlow.value?.takeIf { it.accessCode == accessCode }!!
    }

    override suspend fun submitLocationVote(
        accessCode: String,
        voterId: String,
        location: String
    ): Try<Unit> = Try {
        val game = gameFlow.value ?: return illegalState("Game is null when voting")
        updateGame {
            it.copy(players = it.players.map { player ->
                if (player.id == voterId) {
                    player.copy(votedCorrectly = location == game.locationName)
                } else {
                    player
                }
            })
        }
            .logOnError()
    }

    override suspend fun submitOddOneOutVote(
        accessCode: String,
        voterId: String,
        voteId: String
    ): Try<Unit> {
        return updateGame {
            val oddOneOutId = it.players.find { p -> p.isOddOneOut }?.id
                ?: throw IllegalStateException("No odd one out")

            it.copy(players = it.players.map { player ->
                if (player.id == voterId) {
                    player.copy(votedCorrectly = voteId == oddOneOutId)
                } else {
                    player
                }
            })
        }
    }

    private suspend fun updateGame(update: (Game) -> Game): Try<Unit> {
        val currentGame = gameFlow.value ?: return illegalState("Offline Game not in state")
        return Try {
            val updatedGame = update(currentGame)
            datastore.updateData {
                it.toMutablePreferences().apply {
                    this[gamePreferenceKey] = jsonAdapter.toJson(updatedGame)
                }
            }
        }
            .throwIfDebug()
            .logOnError("Could not update the single device game")
            .ignoreValue()
    }

    companion object {
        private val gamePreferenceKey = stringPreferencesKey("game")
        const val name = "SingleDeviceRepository"
    }
}