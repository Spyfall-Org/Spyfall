package com.dangerfield.libraries.game

import kotlinx.coroutines.flow.Flow
import spyfallx.core.Try
import java.lang.Exception

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
    )

    suspend fun doesGameExist(accessCode: String): Try<Boolean>

    suspend fun end(accessCode: String)
    suspend fun start(accessCode: String)
    suspend fun reset(accessCode: String)
    suspend fun changeName(newName: String, player: Player)

    suspend fun assignRoles(accessCode: String)

    suspend fun getGameFlow(accessCode: String): Flow<Game>
    suspend fun getGame(accessCode: String): Try<Game>
}