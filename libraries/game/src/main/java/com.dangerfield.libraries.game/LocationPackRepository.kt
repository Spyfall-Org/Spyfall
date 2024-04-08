package com.dangerfield.libraries.game

import oddoneout.core.Catching

interface LocationPackRepository {
    suspend fun getPacks(
        language: String,
        packsVersion: Int
    ): Catching<List<LocationPack>>

    suspend fun getPack(
        language: String,
        packsVersion: Int,
        packName: String
    ): Catching<LocationPack>

    suspend fun getLocations(
        language: String,
        packsVersion: Int,
        packName: String
    ): Catching<List<Location>>

    suspend fun getRoles(
        language: String,
        packsVersion: Int,
        locationName: String
    ): Catching<List<String>>
}