package com.dangerfield.libraries.game

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationPack(
    val name: String,
    val locations: List<Location>
)