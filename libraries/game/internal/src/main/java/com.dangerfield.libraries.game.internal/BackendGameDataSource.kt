package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.Player
import kotlinx.coroutines.flow.Flow
import oddoneout.core.Catching

interface BackendGameDataSource {
    suspend fun setGame(game: Game)
    suspend fun getGame(accessCode: String): Catching<BackendGame>
    suspend fun subscribeToGame(accessCode: String): Flow<Catching<BackendGame>>
    suspend fun removePlayer(accessCode: String, id: String): Catching<Unit>
    suspend fun addPlayer(accessCode: String, player: Player)
    suspend fun setLocation(accessCode: String, location: String)
    suspend fun delete(accessCode: String): Catching<Unit>
    suspend fun setGameBeingStarted(accessCode: String, isBeingStarted: Boolean): Catching<Unit>
    suspend fun setStartedAt(accessCode: String): Catching<Unit>
    suspend fun updatePlayers(accessCode: String, list: List<Player>): Catching<Unit>
    suspend fun changeName(accessCode: String, newName: String, id: String): Catching<Unit>
    suspend fun setHost(accessCode: String, id: String): Catching<Unit>
    suspend fun setPlayerVotedCorrectly(accessCode: String, playerId: String, votedCorrectly: Boolean): Catching<Unit>
}

fun Game.toBackEndGame(): BackendGame {
    return BackendGame(
        accessCode = accessCode,
        startedAt = startedAt,
        isBeingStarted = isBeingStarted,
        players = players.map { it.toBackEndPlayer() },
        secretItemName = secretItem.name,
        items = secretOptions,
        packIds = packs.map { it.id },
        languageCode = languageCode,
        lastActiveAt = lastActiveAt,
        gameVersion = version,
        timeLimitSeconds = timeLimitSeconds,
        packsVersion = packsVersion,
        videoCallLink = videoCallLink
    )
}

fun Player.toBackEndPlayer(): BackendPlayer {
    return BackendPlayer(
        id = id,
        isHost = isHost,
        isOddOneOut = isOddOneOut,
        role = role,
        userName = userName,
        votedCorrectly = votedCorrectly
    )
}

fun BackendPlayer.toPlayer(): Player {
    return Player(
        id = id,
        isHost = isHost,
        isOddOneOut = isOddOneOut,
        role = role,
        userName = userName,
        votedCorrectly = votedCorrectly
    )
}

fun List<BackendPlayer>.toPlayers(): List<Player> {
    return map { it.toPlayer() }
}

fun List<Player>.toBackEndPlayers(): List<BackendPlayer> {
    return map { it.toBackEndPlayer() }
}

data class BackendGame(
    val accessCode: String,
    val startedAt: Long?,
    val isBeingStarted: Boolean,
    val players: List<BackendPlayer>,
    val secretItemName: String,
    val items: List<String>,
    val packIds: List<String>,
    val languageCode : String,
    val lastActiveAt: Long?,
    val gameVersion: Int,
    val timeLimitSeconds: Int,
    val packsVersion: Int,
    val videoCallLink: String?,
)

fun List<BackendPlayer>.hasEveryoneVoted(): Boolean {
    return all { it.votedCorrectly != null }
}

data class BackendPlayer(
    val id: String,
    val isHost: Boolean,
    val isOddOneOut: Boolean,
    val role: String?,
    val userName: String,
    val votedCorrectly: Boolean?,
)
