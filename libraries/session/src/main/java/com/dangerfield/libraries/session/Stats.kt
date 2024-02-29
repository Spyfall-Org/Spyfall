package com.dangerfield.libraries.session

import com.squareup.moshi.JsonClass

data class Stats(
    val multiDeviceGamesPlayed: Int,
    val winsAsOddOne: List<GameKey>,
    val winsAsPlayer: List<GameKey>,
    val lossesAsOddOne: List<GameKey>,
    val lossesAsPlayer: List<GameKey>,
    val singleDeviceGamesPlayed: Int,
)

@JsonClass(generateAdapter = true)
data class GameKey(val gameId: String, val startedAt: Long)
