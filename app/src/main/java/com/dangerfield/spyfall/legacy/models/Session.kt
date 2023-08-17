package com.dangerfield.spyfall.legacy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Session(
    val accessCode: String,
    var currentUser: String,
    var game: Game,
    var previousUserName: String = currentUser
) : Parcelable {

    fun updateCurrentUsername(newName: String) {
        previousUserName = currentUser
        currentUser = newName
    }

    override fun toString(): String {
        return "(accessCode: $accessCode, username: $currentUser, prevUsername: $previousUserName) game: $game"
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }
}
