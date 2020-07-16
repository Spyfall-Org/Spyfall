package com.dangerfield.spyfall.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

interface SessionListener {
    fun onSessionEnded()
    fun onGameUpdates(game: Game)
}

@Parcelize
@Entity(tableName = "CURRENT_SESSION")
class CurrentSession (
    @PrimaryKey val accessCode: String,
    var currentUser: String,
    var game : Game
) : Parcelable {
constructor() : this("","", Game("", arrayListOf(),false, arrayListOf(), arrayListOf(),0L,
    arrayListOf()))
}
