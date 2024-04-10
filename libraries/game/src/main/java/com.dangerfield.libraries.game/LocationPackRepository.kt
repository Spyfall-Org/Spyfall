package com.dangerfield.libraries.game

import oddoneout.core.Catching

interface LocationPackRepository {
    /**
     * Gets the location packs for the given language code and version. Attempts to pull from cache
     * otherwise fetches new and then caches for next time.
     *
     * If the network fails we will attempt to get the fallback location packs.
     * This will be considered a `Miss` as we are returning a different version of the location packs.
     *
     * @param languageCode The language code of the location packs to get. See [com.dangerfield.libraries.dictionary.GetAppLanguageCode]
     * @param version The version of the location packs to get.
     */
    suspend fun getPacks(
        languageCode: String,
        version: Int
    ): Catching<LocationPacksResult>

    suspend fun getPack(
        languageCode: String,
        version: Int,
        name: String
    ): Catching<LocationPack>

    suspend fun getRoles(
        languageCode: String,
        version: Int,
        location: String
    ): Catching<List<String>>
}

sealed class LocationPacksResult(val locationPacks: List<LocationPack>) {
    data class Hit(
        val packs: List<LocationPack>
    ) : LocationPacksResult(packs)

    data class Miss(
        val version: Int,
        val packs: List<LocationPack>
    ) : LocationPacksResult(packs)
}

fun Catching<LocationPacksResult>.successfulHitOrThrow(): LocationPacksResult.Hit {
    val value = this.getOrThrow()
    return value as? LocationPacksResult.Hit ?: throw IllegalStateException("Expected Hit but got Miss")
}