package spyfallx.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Session(
    val accessCode: String,
    var user: User,
) : Parcelable
