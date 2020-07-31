package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.models.Game

interface SessionUpdater {
    fun onSessionEnded()
    fun onSessionGameUpdates(game: Game)
}