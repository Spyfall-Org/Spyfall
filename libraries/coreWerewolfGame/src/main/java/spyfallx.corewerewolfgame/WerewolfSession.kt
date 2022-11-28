package spyfallx.corewerewolfgame

import spyfallx.coregameapi.Session

class WerewolfSession(
    override val accessCode: String,
    override val player: WerewolfPlayer
) : Session
