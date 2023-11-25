package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.Player
import spyfallx.core.Try

interface GameDataSource {
    suspend fun setGame(accessCode: String, game: Game)
    suspend fun getGame(accessCode: String): Try<Game>
    suspend fun removePlayer(accessCode: String, player: Player)
    suspend fun addPlayer(accessCode: String, player: Player)
    suspend fun setLocation(accessCode: String, location: String)
    suspend fun endGame(accessCode: String)
    suspend fun setStarted(accessCode: String, started: Boolean)
    suspend fun setPlayers(accessCode: String, list: List<Player>)
}
