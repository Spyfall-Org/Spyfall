package com.dangerfield.oddoneout.legacy.api

import android.content.Context

class Constants(val context: Context) {
    val games: String = "games"
    val packs = "packs"
    val configCollection = "config"
    val requiredVersionCodeDocument = "required_version_code"
    val requiredVersionCodeField = "code"
    val feedback = "feedback"

    object StatisticsConstants {
        const val collection = "stats"
        const val document = "game"
        const val num_android_players = "android_num_of_players"
        const val num_games_played = "num_games_played"
    }

    object GameFields {
        const val playerObjectList = "playerObjectList"
        const val playerList = "playerList"
        const val theSpyRole = "The Spy!"
        const val started = "started"
        const val expiration = "expiration"
        const val chosenLocation = "chosenLocation"
    }
}


