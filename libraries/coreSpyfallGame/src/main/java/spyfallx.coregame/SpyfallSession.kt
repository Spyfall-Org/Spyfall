package spyfallx.coregame

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import spyfallx.coregameapi.Session

@Parcelize
class SpyfallSession(
    override val accessCode: String,
    override val player: SpyfallPlayer
) : Session, Parcelable
