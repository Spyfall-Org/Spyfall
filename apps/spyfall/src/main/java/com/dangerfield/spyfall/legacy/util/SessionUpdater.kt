package com.dangerfield.spyfall.legacy.util

import com.dangerfield.spyfall.legacy.models.Game

interface SessionUpdater {
    fun onSessionEnded()
    fun onSessionGameUpdates(game: Game)
}
