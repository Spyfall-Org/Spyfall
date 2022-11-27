package spyfallx.coregame

import kotlinx.parcelize.Parcelize
import spyfallx.coregameapi.Player

@Parcelize
data class SpyfallPlayer(
    override var username: String,
    override var id: String,
    override val isHost: Boolean,
    val role: String?,
) : Player
