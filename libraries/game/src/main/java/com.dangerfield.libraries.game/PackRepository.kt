package com.dangerfield.libraries.game

import oddoneout.core.Catching

interface PackRepository {


    /**
     * Returns the packs that are not custom or community. These packs come with the app.
     * The return is dependent on the sync operation finishing to ensure the latest version is
     * fetched. If that call fails then the last known version is returned.
     */
    suspend fun getAppPacks(version: Int, languageCode: String): Catching<PackResult>

    /**
     * Returns packs that are saved for the user. These are packs they created or saved from the community
     * Not every pack the user plays should be considered saved for the user. Saved packs represent packs
     * the user  wants to keep around.
     */
    suspend fun getUsersSavedPacks(): Catching<List<Pack<PackItem>>>

    /**
     * Saves a pack for the user.
     * This is a pack they created or saved from the community
     */
    suspend fun savePack(packId: String): Catching<Unit>

    /**
     * Updates the last accessed time for a pack
     */
    suspend fun updateLastAccessed(packId: String): Catching<Unit>

    /**
     * Gets the pack item for the given pack item name, version and language code
     */
    suspend fun getPackItem(
        itemName: String,
        version: Int,
        languageCode: String
    ):  Catching<PackItem?>

    /**
     * Returns the specific pack
     * The most likely caller of this is someone joining a game right?
     * So this should also check backend in which case we need the version and language code
     * and id, not just the id.
     *
     *
     * Called when:
     * - Getting roles for reassigning
     * - Joining a game
     */
    suspend fun getPack(
        version: Int,
        languageCode: String,
        id: String
    ): Catching<Pack<PackItem>>
}

sealed class PackResult(val packs: List<Pack<PackItem>>) {
    class Hit(
        packs: List<Pack<PackItem>>
    ) : PackResult(packs)

    class Miss(
        val version: Int,
        packs: List<Pack<PackItem>>
    ) : PackResult(packs)
}

fun <T: PackItem> Catching<PackResult>.hitOrThrow(): PackResult.Hit {
    val value = this.getOrThrow()
    return value as? PackResult.Hit ?: throw IllegalStateException("Expected Hit but got Miss")
}