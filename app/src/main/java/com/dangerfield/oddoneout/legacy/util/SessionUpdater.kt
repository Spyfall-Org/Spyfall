package com.dangerfield.oddoneout.legacy.util

import com.dangerfield.oddoneout.legacy.models.Game

interface SessionUpdater {
    fun onSessionEnded()
    fun onSessionGameUpdates(game: Game)
}
