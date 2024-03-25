package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.Player
import kotlinx.coroutines.flow.Flow
import oddoneout.core.Catching

interface GameDataSource {
    suspend fun setGame(game: Game)
    suspend fun getGame(accessCode: String): Catching<Game>
    suspend fun subscribeToGame(accessCode: String): Flow<Catching<Game>>
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