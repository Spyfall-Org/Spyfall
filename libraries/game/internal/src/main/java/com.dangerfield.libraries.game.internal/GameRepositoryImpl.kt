package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.Player
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import spyfallx.core.failure
import spyfallx.core.success
import javax.inject.Inject

@AutoBind
class GameRepositoryImpl @Inject constructor(
    private val gameDataSource: GameDataSource
) : GameRepository {

    override suspend fun create(game: Game): Try<Unit> = Try {
        gameDataSource.setGame(game)
    }

    override suspend fun join(accessCode: String, userId: String, userName: String): Try<Unit> =
        Try {
            gameDataSource.addPlayer(
                accessCode, Player(
                    id = userId,
                    role = null,
                    userName = userName,
                    isSpy = false,
                    isHost = false
                )
            )
        }

    override suspend fun removeUser(accessCode: String, username: String) {
        TODO("Not yet implemented")
    }

    override suspend fun doesGameExist(accessCode: String): Try<Boolean> {
        return gameDataSource.getGame(accessCode).fold(
            onSuccess = { true.success() },
            onFailure = {
                if (it is GameError.GameNotFound) {
                    false.success()
                } else {
                    it.failure()
                }
            }
        )
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