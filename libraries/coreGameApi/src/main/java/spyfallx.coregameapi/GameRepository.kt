package spyfallx.coregameapi

import kotlinx.coroutines.flow.Flow
import spyfallx.core.Game

interface GameRepository {
    fun getGame(accessCode: String) : Flow<Game>
    suspend fun gameExists(accessCode: String) : Boolean
}
