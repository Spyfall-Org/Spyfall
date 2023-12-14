package com.dangerfield.libraries.game

import kotlinx.coroutines.flow.Flow
import spyfallx.core.Try

interface GameRepository {

    suspend fun create(game: Game): Try<Unit>

    suspend fun join(
        accessCode: String,
        userId: String,
        userName: String
    ): Try<Unit>

    suspend fun removeUser(
        accessCode: String,
        username: String
    ): Try<Unit>

    suspend fun doesGameExist(accessCode: String): Try<Boolean>

    suspend fun end(accessCode: String)
    suspend fun start(accessCode: String): Try<Unit>
    suspend fun setGameIsBeingStarted(accessCode: String, isBeingStarted: Boolean): Try<Unit>
    suspend fun reset(accessCode: String): Try<Unit>
    suspend fun changeName(accessCode: String, newName: String, id: String): Try<Unit>

    suspend fun updatePlayers(accessCode: String, players: List<Player>): Try<Unit>

    fun getGameFlow(accessCode: String): Flow<Game>
    suspend fun getGame(accessCode: String): Try<Game>
    suspend fun submitLocationVote(accessCode: String, voterId: String,  location: String): Try<Unit>
    suspend fun submitOddOneOutVote(accessCode: String, voterId: String, voteId: String): Try<Unit>
}