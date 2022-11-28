package spyfallx.corewerewolfgame

sealed class MinionStatus {
    object NonMinion : MinionStatus()
    class Minion(val hasAcknowledged: Boolean)
}
