package spyfallx.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Player(var role: String, var username: String, var votes: Int) : Parcelable