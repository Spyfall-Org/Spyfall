package com.dangerfield.libraries.game

import kotlinx.coroutines.flow.Flow
import spyfallx.core.Try
import java.lang.Exception

interface GameRepository {

    suspend fun create(
        username: String,
        timeLimitMins: Long,
        packs: List<String>
    )

    suspend fun join(
        accessCode: String,
        id: String,
        userName: String
    ): Try<Unit>

    suspend fun removeUser(
        accessCode: String,
        username: String
    )

    suspend fun end(accessCode: String)
    suspend fun start(accessCode: String)
    suspend fun reset(accessCode: String)
    suspend fun changeName(newName: String, player: Player)

    suspend fun assignRoles(accessCode: String)

    suspend fun getGameFlow(accessCode: String): Flow<Game>
    suspend fun getGame(accessCode: String): Try<Game>
}