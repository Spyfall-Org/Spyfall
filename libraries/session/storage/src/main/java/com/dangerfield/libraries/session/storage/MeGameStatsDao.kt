package com.dangerfield.libraries.session.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dangerfield.libraries.session.GameKey
import kotlinx.coroutines.flow.Flow

@Dao
interface MeGameStatsDao {

    ////////////////// GAME RESULTS //////////////////////

    @Query("SELECT * FROM MeGameResult")
    fun getGameResultsFlow(): Flow<List<MeGameResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGameResult(result: MeGameResult)

    @Query("DELETE FROM MeGameResult WHERE gameKey = :gameKey")
    suspend fun deleteGameResult(gameKey: GameKey)

    ////////////////// GAMES PLAYED //////////////////////

    @Query("SELECT * FROM MeGamePlayed")
    fun getGamePlayedFlow(): Flow<List<MeGamePlayed>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGamePlayed(gamePlayed: MeGamePlayed)

    @Query("DELETE FROM MeGamePlayed WHERE gameKey = :gameKey")
    suspend fun deleteGamePlayed(gameKey: GameKey)

}