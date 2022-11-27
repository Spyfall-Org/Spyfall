package spyfallx.coregameapi

interface Game {
    val startedAt: Long
    val players: List<Player>
    val accessCode: String
    val isOpenToJoin: Boolean
}


