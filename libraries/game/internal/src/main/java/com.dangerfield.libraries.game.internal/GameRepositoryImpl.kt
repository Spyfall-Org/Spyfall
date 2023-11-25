package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.Player
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import javax.inject.Inject

@AutoBind
class GameRepositoryImpl @Inject constructor(
    private val gameDataSource: GameDataSource
) : GameRepository {

    override suspend fun create(username: String, timeLimitMins: Long, packs: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun join(accessCode: String, id: String, userName: String): Try<Unit> = Try {
        gameDataSource.addPlayer(
            accessCode, Player(
                id = id,
                role = null,
                userName = userName,
                isSpy = false
            )
        )
    }

    override suspend fun removeUser(accessCode: String, username: String) {
        TODO("Not yet implemented")
    }

    override suspend fun end(accessCode: String) {
        TODO("Not yet implemented")
    }

    override suspend fun start(accessCode: String) {
        TODO("Not yet implemented")
    }

    override suspend fun reset(accessCode: String) {
        TODO("Not yet implemented")
    }

    override suspend fun changeName(newName: String, player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun assignRoles(accessCode: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getGameFlow(accessCode: String): Flow<Game> {
        TODO("Not yet implemented")
    }

    override suspend fun getGame(accessCode: String): Try<Game> = gameDataSource.getGame(accessCode)

}