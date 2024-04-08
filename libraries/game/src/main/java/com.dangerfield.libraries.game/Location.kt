package com.dangerfield.libraries.game

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    val name: String,
    val roles: List<String>,
    val packName: String
)
