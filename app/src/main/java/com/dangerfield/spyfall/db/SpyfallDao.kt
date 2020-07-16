package com.dangerfield.spyfall.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dangerfield.spyfall.models.CurrentSession

@Dao
interface SpyfallDao {

    @Query("SELECT * from CURRENT_SESSION")
    fun getCurrentSession(): List<CurrentSession>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentSession(session: CurrentSession)

    @Query("DELETE from CURRENT_SESSION")
    fun cleanCurrentSession ()
}