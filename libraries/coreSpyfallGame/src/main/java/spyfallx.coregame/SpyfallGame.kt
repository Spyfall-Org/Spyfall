package spyfallx.coregame

import spyfallx.coregameapi.Game

data class SpyfallGame(
    override val startedAt: Long,
    override val accessCode: String,
    override val isOpenToJoin: Boolean,
    override val players: List<SpyfallPlayer>,
    var chosenLocation: String,
    var chosenPacks: ArrayList<String>,
    var timeLimit: Long,
    var locationList: ArrayList<String>
) : Game
