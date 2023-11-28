package com.dangerfield.libraries.game

import spyfallx.core.Try

interface LocationPackRepository {
    suspend fun getPacks(): Try<List<Pack>>

    suspend fun getPack(packName: String): Try<Pack>

    suspend fun getLocations(packName: String): Try<List<Location>>

    suspend fun getRoles(locationName: String): Try<List<String>>
}