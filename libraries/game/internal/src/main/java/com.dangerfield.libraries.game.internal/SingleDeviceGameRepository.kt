package com.dangerfield.libraries.game.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayItems
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.storage.datastore.distinctKeyFlow
import com.dangerfield.oddoneoout.libraries.game.internal.R
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

@AutoBind
@Singleton
@Named(SingleDeviceRepositoryName)
class SingleDeviceGameRepository
@Inject constructor(
    private val datastore: DataStore<Preferences>,
    private val clock: Clock,
    private val packRepository: PackRepository,
    private val getGamePlayItems: GetGamePlayItems,
    private val gameConfig: GameConfig,
    private val dictionary: Dictionary,
    @ApplicationScope private val applicationScope: CoroutineScope,
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

        val packs = currentGame.packIds.mapNotNull { packId ->
            packRepository.getPack(
                languageCode = session.user.languageCode,
                version = currentGame.packsVersion,
                id = packId
            )
                .throwIfDebug()
                .getOrNull()
        }

        val locations = getGamePlayItems(packs = packs, isSingleDevice = true).getOrThrow()
        val location = locations.random()

        val shuffledPlayerWithRoles = getUpdatedPlayers(location.roles, currentGame.players)

        val game = Game(
            secret = location.name,
            packIds = packs.map { it.id },
            isBeingStarted = false,
            players = shuffledPlayerWithRoles,
            timeLimitMins = currentGame.timeLimitMins,
            startedAt = null,
            secretOptions = locations.map { it.name },
            videoCallLink = null,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis(),
            packsVersion = gameConfig.packsVersion,
            languageCode = session.user.languageCode
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
        val defaultRole = roles?.first()

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
                    player.copy(votedCorrectly = location == game.secret)
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