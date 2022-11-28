package spyfallx.corewerewolfgame

import kotlinx.coroutines.flow.Flow
import spyfallx.coregameapi.GameRepository
import javax.inject.Inject

class WerewolfRepository @Inject constructor() : GameRepository<WerewolfGame> {
    override fun getGame(accessCode: String): Flow<WerewolfGame> {
        TODO("Not yet implemented")
    }

    override suspend fun gameExists(accessCode: String): Boolean {
        TODO("Not yet implemented")
    }
}
