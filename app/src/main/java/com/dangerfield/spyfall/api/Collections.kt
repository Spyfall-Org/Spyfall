package com.dangerfield.spyfall.api

import com.dangerfield.spyfall.BuildConfig

object Collections {
    val games: String
        get() = if (BuildConfig.DEBUG) "games_test" else "games"

    val packs = "packs"
}

object StatisticsConstants {
    val collection = "stats"
    val document = "game"
    val num_android_players = "android_num_of_players"
    val num_games_played = "num_games_played"
}

