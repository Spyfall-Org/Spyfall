package com.dangerfield.libraries.game

import oddoneout.core.Try

interface LocationPackRepository {
    suspend fun getPacks(): Try<List<LocationPack>>

    suspend fun getPack(packName: String): Try<LocationPack>

    suspend fun getLocations(packName: String): Try<List<Location>>

    suspend fun getRoles(locationName: String): Try<List<String>>
}