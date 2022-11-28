package spyfallx.corewerewolfgame

import spyfallx.coregameapi.Game

class WerewolfGame(
    override val startedAt: Long?,
    override val accessCode: String,
    override val isOpenToJoin: Boolean,
    override val players: List<WerewolfPlayer>,
    val dayTimeLimit: Long,
    val currentDayNumber: Int,
    val accusation: Accusation?
) : Game
