package spyfallx.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Session (
    val accessCode: String,
    var user: User,
) : Parcelable