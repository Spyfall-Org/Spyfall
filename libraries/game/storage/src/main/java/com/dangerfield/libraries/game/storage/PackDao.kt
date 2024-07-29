package com.dangerfield.libraries.game.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface PackDao {

    // TODO do I need to do take 1? or return a list? look at each query
    @Delete
    suspend fun deletePacks(packs: List<PackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPacks(packs: List<PackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackItems(packItem: List<PackItemEntity>)

    @Query("SELECT * FROM packs WHERE id in (:ids)")
    suspend fun getPacks(ids: List<String>): List<PackEntity>

    @Query("SELECT * FROM packs")
    suspend fun getAllPacks(): List<PackEntity>

    @Transaction
    @Query("SELECT * FROM packs WHERE dbPackOwner == :owner AND version = :version AND languageCode = :languageCode")
    suspend fun getPacksWithItems(
        version: Int,
        languageCode: String,
        owner: DbPackOwner
    ): List<PackWithItems>

    @Transaction
    @Query("SELECT * FROM packs WHERE dbPackOwner == :owner AND languageCode = :languageCode")
    suspend fun getPacksWithItems(
        languageCode: String,
        owner: DbPackOwner
    ): List<PackWithItems>

    @Transaction
    @Query("SELECT * FROM packs WHERE id == :id")
    suspend fun getPacksWithItems(id: String): PackWithItems

    @Transaction
    @Query("SELECT * FROM packs WHERE languageCode == :languageCode AND version = :version")
    suspend fun getPacksWithItems(languageCode: String, version: Int): List<PackWithItems>

    @Transaction
    @Query("SELECT * FROM packs WHERE isUserSaved = 1")
    suspend fun getUserSavedPacksWithItems(): List<PackWithItems>

    @Query("SELECT * FROM packs WHERE dbPackOwner == :owner AND languageCode = :languageCode")
    suspend fun getPacks(
        languageCode: String,
        owner: DbPackOwner
    ): List<PackEntity>

    // update pack accessed
    @Query("UPDATE pack_access_records SET lastAccessed = :accessed WHERE id = :packId")
    suspend fun updatePackAccessed(packId: String, accessed: Long)

    @Query("UPDATE packs SET isUserSaved = :isSaved WHERE id = :packId")
    suspend fun setPackSaved(packId: String, isSaved: Boolean)

    // Get all user's saved packs
    @Query("SELECT * FROM packs WHERE isUserSaved = 1")
    suspend fun getUsersSavedPacks(): List<PackEntity>

    @Query("SELECT * FROM pack_access_records")
    suspend fun getAccessRecords(): List<PackAccessRecordEntity>

}