package spyfallx.coregame

import spyfallx.coregameapi.Game

data class SpyfallGame(
    override val startedAt: Long?,
    override val accessCode: String,
    override val isOpenToJoin: Boolean,
    override val players: List<SpyfallPlayer>,
    val chosenLocation: String,
    val chosenPacks: ArrayList<String>,
    val timeLimit: Long,
    val locationList: ArrayList<String>
) : Game
