package com.dangerfield.libraries.game

import oddoneout.core.Catching

interface LocationPackRepository {
    suspend fun getPacks(): Catching<List<LocationPack>>

    suspend fun getPack(packName: String): Catching<LocationPack>

    suspend fun getLocations(packName: String): Catching<List<Location>>

    suspend fun getRoles(locationName: String): Catching<List<String>>
}