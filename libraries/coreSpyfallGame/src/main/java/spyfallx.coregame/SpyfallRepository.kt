package spyfallx.coregame

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import spyfallx.coregameapi.GameRepository
import javax.inject.Inject

class SpyfallRepository @Inject constructor() : GameRepository<SpyfallGame> {

    override fun getGame(accessCode: String): Flow<SpyfallGame> = flow {}

    override suspend fun gameExists(accessCode: String): Boolean {
        delay(5000)
        return false
    }
}
