package com.dangerfield.libraries.game

interface LocationPackRepository {
    fun getPacks(): List<Pack>

    fun getPack(packName: String): Pack?

    fun getLocations(packName: String): List<Location>?

    fun getRoles(locationName: String): List<String>?
}