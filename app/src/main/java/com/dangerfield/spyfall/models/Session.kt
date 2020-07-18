package com.dangerfield.spyfall.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

interface SessionListener {
    fun onSessionEnded()
    fun onGameUpdates(game: Game)
}

@Parcelize
class Session (
    val accessCode: String,
    var currentUser: String,
    var game : Game,
    var previousUserName: String = currentUser
) : Parcelable {

    fun updateCurrentUsername(newName: String) {
        previousUserName = currentUser
        currentUser = newName
    }

    override fun toString(): String {
        return "(accessCode: $accessCode, username: $currentUser)"
    }
}
