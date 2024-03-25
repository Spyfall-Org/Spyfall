package com.dangerfield.libraries.game.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.PacksVersion
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.storage.datastore.distinctKeyFlow
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import oddoneout.core.Catching
import oddoneout.core.developerSnackOnError
import oddoneout.core.doNothing
import oddoneout.core.ignoreValue
import oddoneout.core.illegalStateFailure
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug
import se.ansman.dagger.auto.AutoBind
import java.time.Clock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Named(SingleDeviceRepositoryName)
@AutoBind
@Singleton
class SingleDeviceGameRepository
@Inject constructor(
    private val datastore: DataStore<Preferences>,
    private val clock: Clock,
    private val locationPackRepository: LocationPackRepository,
    private val getGamePlayLocations: GetGamePlayLocations,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val packsVersion: PacksVersion,
    private val session: Session,
    moshi: Moshi,
) : GameRepository {
    private val jsonAdapter = moshi.adapter(Game::class.java)

    private val gameFlow = datastore
        .distinctKeyFlow(gamePreferenceKey)
        .map {
            it?.let { json ->
                Catching { jsonAdapter.fromJson(json) }
                    .logOnFailure()
                    .throwIfDebug()
                    .getOrNull()
            }
        }
        .stateIn(
            applicationScope,
            SharingStarted.Eagerly,
            null
        )

    override suspend fun create(game: Game): Catching<Unit> = Catching {
        datastore.updateData {
            it.toMutablePreferences().apply {
                val gameJson = jsonAdapter.toJson(game)
                this[gamePreferenceKey] = gameJson
            }
        }
    }
        .throwIfDebug()
        .ignoreValue()

    override suspend fun join(accessCode: String, userId: String, userName: String): Catching<Unit> =
        Catching {
            // single device games cannot be joined
            doNothing()
        }

    override suspend fun removeUser(accessCode: String, username: String): Catching<Unit> = Catching {
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
        gameFlow.value?.accessCode == accessCode
    }

    override suspend fun end(accessCode: String) {
        Catching {
            datastore.updateData {
                it.toMutablePreferences().apply {
                    remove(gamePreferenceKey)
                }
            }
        }
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

    // TODO extract out logic shared from this
    override suspend fun reset(accessCode: String): Catching<Unit> = Catching {
        val currentGame = gameFlow.value.takeIf { it?.accessCode == accessCode }
            ?: return illegalStateFailure { "Single Device Game is null when resetting" }

        val packs = currentGame.packNames.mapNotNull { packName ->
            locationPackRepository.getPack(packName)
                .throwIfDebug()
                .getOrNull()
        }

        val locations = getGamePlayLocations(locationPacks = packs, isSingleDevice = true).getOrThrow()
        val location = locations.random()
        val shuffledRoles = location.roles.shuffled()

        val players = currentGame.players.map {
            it.copy(
                role = null,
                isOddOneOut = false,
                votedCorrectly = null
            )
        }

        val oddOneOutIndex = players.indices.random()

        val playersWithRoles = players.mapIndexed { index, player ->
            val role = if (index == oddOneOutIndex) "The Odd One Out" else shuffledRoles[index]
            player.copy(role = role, isOddOneOut = index == oddOneOutIndex)
        }

        val game = Game(
            locationName = location.name,
            packNames = packs.map { it.name },
            isBeingStarted = false,
            players = playersWithRoles,
            timeLimitMins = currentGame.timeLimitMins,
            startedAt = null,
            locationOptionNames = locations.map { it.name },
            videoCallLink = null,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis(),
            packsVersion = packsVersion(),
            languageCode = session.user.languageCode
        )

        return updateGame { game }
            .ignoreValue()
            .logOnFailure()
            .developerSnackOnError { "Could not reset game" }
    }
        .logOnFailure("Could not reset game")
        .throwIfDebug()

    override suspend fun changeName(accessCode: String, newName: String, id: String): Catching<Unit> =
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

    override fun getGameFlow(accessCode: String): Flow<Game> =
        gameFlow
            .filterNotNull()
            .filter { it.accessCode == accessCode }

    override suspend fun getGame(accessCode: String): Catching<Game> = Catching {
        gameFlow.value?.takeIf { it.accessCode == accessCode }!!
    }

    override suspend fun submitLocationVote(
        accessCode: String,
        voterId: String,
        location: String
    ): Catching<Unit> = Catching {
        val game = gameFlow.value ?: return illegalStateFailure { "Game is null when voting" }
        updateGame {
            it.copy(players = it.players.map { player ->
                if (player.id == voterId) {
                    player.copy(votedCorrectly = location == game.locationName)
                } else {
                    player
                }
            })
        }
            .logOnFailure()
    }

    override suspend fun submitOddOneOutVote(
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
        val currentGame = gameFlow.value?.withUpdatedLastActiveAt()
            ?: return illegalStateFailure { "Offline Game not in state" }

        return Catching {
            val updatedGame = update(currentGame)
            datastore.updateData {
                it.toMutablePreferences().apply {
                    this[gamePreferenceKey] = jsonAdapter.toJson(updatedGame)
                }
            }
            updatedGame
        }
            .throwIfDebug()
            .logOnFailure("Could not update the single device game")
    }

    companion object {
        private val gamePreferenceKey = stringPreferencesKey("game")
        const val name = "SingleDeviceRepository"
    }
}