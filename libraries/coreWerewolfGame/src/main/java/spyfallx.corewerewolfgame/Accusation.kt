package spyfallx.corewerewolfgame

data class Accusation(
    val accuser: WerewolfPlayer,
    val accused: WerewolfPlayer,
    val status: AccusationStatus
)

sealed class AccusationStatus {
    class Accusation(val startedAt: Long, val timeLimit: Long) : AccusationStatus()
    class Defense(val startedAt: Long, val timeLimit: Long) : AccusationStatus()
    class Voting(val startedAt: Long, val timeLimit: Long, val yays: Int, val nays: Int) : AccusationStatus()
    sealed class Resolved : AccusationStatus() {
        object AccusedKilled : Resolved()
        object AccusedNotKilled : Resolved()
    }
}
