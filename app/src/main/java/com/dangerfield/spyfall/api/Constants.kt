package com.dangerfield.spyfall.api

import android.content.Context
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.util.PreferencesService

class Constants(val context: Context, private val preferencesHelper: PreferencesService) {
    val games: String
        get() = if (BuildConfig.DEBUG && preferencesHelper.getUseTestDbState()) games_test else games_prod

    val games_test = "games_test"
    val games_prod = "games"

    val packs = "packs"

    val feedback = if (BuildConfig.DEBUG) "feedback_test" else "feedback"

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


