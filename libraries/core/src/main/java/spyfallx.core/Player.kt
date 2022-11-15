package spyfallx.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Player(var role: String, var username: String, var votes: Int) : Parcelable
