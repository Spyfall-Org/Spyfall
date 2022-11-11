package spyfallx.coregame

import spyfallx.coregameapi.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import spyfallx.core.Game

class SpyfallRepository : GameRepository {

    override fun getGame(accessCode: String): Flow<Game> = flow {

    }

    override suspend fun gameExists(accessCode: String): Boolean {
        delay(1000)
        return false
    }
}