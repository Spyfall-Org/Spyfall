package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.Player
import kotlinx.coroutines.flow.Flow
import oddoneout.core.Try

interface GameDataSource {
    suspend fun setGame(game: Game)
    suspend fun getGame(accessCode: String): Try<Game>
    suspend fun subscribeToGame(accessCode: String): Flow<Try<Game>>
    suspend fun removePlayer(accessCode: String, id: String): Try<Unit>
    suspend fun addPlayer(accessCode: String, player: Player)
    suspend fun setLocation(accessCode: String, location: String)
    suspend fun delete(accessCode: String): Try<Unit>
    suspend fun setGameBeingStarted(accessCode: String, isBeingStarted: Boolean): Try<Unit>
    suspend fun setStartedAt(accessCode: String): Try<Unit>
    suspend fun updatePlayers(accessCode: String, list: List<Player>): Try<Unit>
    suspend fun changeName(accessCode: String, newName: String, id: String): Try<Unit>
    suspend fun setPlayerVotedCorrectly(accessCode: String, playerId: String, votedCorrectly: Boolean): Try<Unit>
}