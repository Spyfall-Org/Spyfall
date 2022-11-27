package spyfallx.coregameapi

import android.os.Parcelable

interface Player : Parcelable {
    val username: String
    val id: String
    val isHost: Boolean
}
