package com.dangerfield.spyfall.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Player(var role: String,val username: String,var votes: Int): Parcelable