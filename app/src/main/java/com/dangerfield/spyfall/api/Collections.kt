package com.dangerfield.spyfall.api

import com.dangerfield.spyfall.BuildConfig

object Collections {
     val games: String
     get() {
         return if(BuildConfig.DEBUG) "games_test" else "games"
     }

     val packs = "packs"
     val stats = "stats"
 }

