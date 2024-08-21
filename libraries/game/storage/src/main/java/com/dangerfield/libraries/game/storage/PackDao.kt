package com.dangerfield.libraries.game.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PackDao {

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
    suspend fun getPackWithItems(id: String): PackWithItems?

    @Transaction
    @Query("SELECT * FROM packs WHERE id == :id")
    fun getPackWithItemsFlow(id: String): Flow<PackWithItems?>

    @Transaction
    @Query("SELECT * FROM packs WHERE languageCode == :languageCode AND version = :version")
    suspend fun getPacksWithItems(languageCode: String, version: Int): List<PackWithItems>

    @Transaction
    @Query("SELECT * FROM packs WHERE isUserSaved = 1")
    suspend fun getUserSavedPacksWithItems(): List<PackWithItems>

    @Transaction
    @Query("SELECT * FROM packs WHERE isUserSaved = 1")
    fun getUserSavedPacksWithItemsFlow(): Flow<List<PackWithItems>>

    @Query("SELECT * FROM packs WHERE dbPackOwner == :owner AND languageCode = :languageCode")
    suspend fun getPacks(
        languageCode: String,
        owner: DbPackOwner
    ): List<PackEntity>

    @Query("SELECT * FROM packs WHERE id = :id")
    suspend fun getPack(id: String): PackEntity?

    // update pack accessed
    @Query("UPDATE pack_access_records SET lastAccessed = :accessed WHERE id = :packId")
    suspend fun updatePackAccessed(packId: String, accessed: Long)

    @Query("UPDATE packs SET isUserSaved = :isSaved WHERE id = :packId")
    suspend fun setPackSaved(packId: String, isSaved: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePack(packEntity: PackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePackItem(packItemEntity: PackItemEntity)

    @Query("DELETE FROM packs WHERE id = :id")
    suspend fun deletePack(id: String)

    @Query("DELETE FROM pack_items WHERE packId = :packId AND name = :itemName")
    suspend fun deletePackItem(packId: String, itemName: String)

    // Get all user's saved packs
    @Query("SELECT * FROM packs WHERE isUserSaved = 1")
    suspend fun getUsersSavedPacks(): List<PackEntity>

    @Query("SELECT * FROM pack_access_records")
    suspend fun getAccessRecords(): List<PackAccessRecordEntity>

    @Query("SELECT * FROM packs WHERE name = :name")
    suspend fun getPackWithName(name: String): PackEntity?

    @Query("UPDATE packs SET hasMeUserPlayed = :hasUserPlayed WHERE id = :packId")
    suspend fun updateHasUserPlayed(packId: String, hasUserPlayed: Boolean)

}