package com.dangerfield.oddoneout.legacy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Player(var role: String, var username: String, var votes: Int) : Parcelable {
    constructor() : this("", "", 0)

    override fun toString(): String {
        return "(username: $username, role: $role)"
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }
}
