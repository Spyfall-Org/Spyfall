package com.dangerfield.spyfall.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

interface SessionListener {
    fun onSessionEnded()
    fun onGameUpdates(game: Game)
}
@Parcelize
class CurrentSession (
    val accessCode: String,
    var currentUser: String,
    var game : Game
) : Parcelable {

    fun isBeingStarted() = game.started


}
