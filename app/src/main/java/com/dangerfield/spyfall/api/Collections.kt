package com.dangerfield.spyfall.api

import com.dangerfield.spyfall.BuildConfig

object Collections {
     val games: String
     get() {
         return if(BuildConfig.DEBUG) "games" else "games_test"
     }

     val packs = "packs"
     val stats = "stats"
 }

