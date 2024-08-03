package com.dangerfield.libraries.game

import kotlinx.coroutines.flow.Flow
import oddoneout.core.Catching

interface GameRepository {

    suspend fun create(game: Game): Catching<Unit>

    suspend fun join(
        accessCode: String,
        userId: String,
        userName: String
    ): Catching<Unit>

    suspend fun removeUser(
        accessCode: String,
        username: String
    ): Catching<Unit>

    suspend fun assignHost(
        accessCode: String,
        id: String
    ): Catching<Unit>

    suspend fun doesGameExist(accessCode: String): Catching<Boolean>

    suspend fun end(accessCode: String)
    suspend fun start(accessCode: String): Catching<Unit>
    suspend fun setGameIsBeingStarted(accessCode: String, isBeingStarted: Boolean): Catching<Unit>
    suspend fun reset(accessCode: String): Catching<Unit>
    suspend fun changeName(accessCode: String, newName: String, id: String): Catching<Unit>

    suspend fun updatePlayers(accessCode: String, players: List<Player>): Catching<Unit>

    /**
     * @return a flow of the game with the matching access code (or null if none exists)
     * This flow will emit the game or its state when it is updated
     */
    fun getGameFlow(accessCode: String): Flow<Game?>
    suspend fun getGame(accessCode: String): Catching<Game>
    suspend fun submitVoteForSecret(accessCode: String, voterId: String, secret: String): Catching<Unit>
    suspend fun submitVoteForOddOneOut(accessCode: String, voterId: String, voteId: String): Catching<Boolean>
}

/**
 * WARNING: This version should be update only if the core model changes. It is used to tell if 2
 * versions of the app are compatible to play together
 */
const val CURRENT_GAME_MODEL_VERSION = 2

/**
 * The version of packs a build should use is determined by the config. If the config fails and
 * there is no cache we will fallback to this value
 */
const val CURRENT_PACKS_VERSION_FALLBACK = 1

const val SingleDeviceRepositoryName = "SingleDeviceRepository"
const val MultiDeviceRepositoryName = "MultiDeviceRepository"