package spyfallx.corewerewolfgame

import spyfallx.coregameapi.Player

sealed class WerewolfPlayer(
    override val username: String,
    override val id: String,
    override val isHost: Boolean,
    val role: String,
    val isAlive: Boolean
) : Player {

    class Werewolf(
        username: String,
        id: String,
        isHost: Boolean,
        isAlive: Boolean,
        role: String,
        val minion: WerewolfPlayer
    ) : WerewolfPlayer(username, id, isHost, role, isAlive,)

    class Villager(
        username: String,
        id: String,
        isHost: Boolean,
        isAlive: Boolean,
        role: String,
        val minionStatus: MinionStatus
    ) : WerewolfPlayer(username, id, isHost, role, isAlive)

    class Mason(
        username: String,
        id: String,
        isHost: Boolean,
        isAlive: Boolean,
        role: String,
        val minionStatus: MinionStatus
    ) : WerewolfPlayer(username, id, isHost, role, isAlive)

    class Seer(
        username: String,
        id: String,
        isHost: Boolean,
        isAlive: Boolean,
        role: String,
        val minionStatus: MinionStatus
    ) : WerewolfPlayer(username, id, isHost, role, isAlive)
}
