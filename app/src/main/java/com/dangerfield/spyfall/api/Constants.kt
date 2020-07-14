package com.dangerfield.spyfall.api

import android.content.Context
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R

class Constants(val context: Context) {
    val games: String
        get() = if (BuildConfig.DEBUG && getUseTestDbState()) "games_test" else "games"

    val packs = "packs"

    object StatisticsConstants {
        val collection = "stats"
        val document = "game"
        val num_android_players = "android_num_of_players"
        val num_games_played = "num_games_played"
    }

    private fun getUseTestDbState(): Boolean {
        return preferences.getBoolean(
            context.resources.getString(R.string.shared_preferences_test_db),
            true
        )
    }

    private val preferences by lazy {
            context.getSharedPreferences(
            context.resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
    }
}


