package spyfallx.coregameapi

import kotlinx.coroutines.flow.Flow

interface GameRepository<T : Game> {
    fun getGame(accessCode: String): Flow<T>
    suspend fun gameExists(accessCode: String): Boolean
}
