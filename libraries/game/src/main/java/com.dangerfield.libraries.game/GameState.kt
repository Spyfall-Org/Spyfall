package com.dangerfield.libraries.game

sealed class GameState(val accessCode: String) {

    /**
     * State representing that the game does not currently exist. That could be because it was
     * never created or because it was deleted/ended
     */
    class DoesNotExist(accessCode: String) : GameState(accessCode)

    /**
     * State representing that the game is waiting for players to join
     * At this point the game can be joined by anyone with the access code
     */
    class Waiting(
        accessCode: String,
        val players: List<Player>,
    ) : GameState(accessCode)

    /**
     * State representing that the game is being started
     * At this point the game can no longer be joined, the starter is assigning roles and updating
     * the game state
     */
    class Starting(
        accessCode: String,
        val players: List<Player>,
    ) : GameState(accessCode)

    /**
     * State representing that the game has been started
     * At this point all roles have been assigned and the game is being played
     */
    class Started(
        accessCode: String,
        val players: List<Player>,
        val startedAt: Long,
        val timeLimitMins: Int,
        val firstPlayer: Player,
        val location: String,
    ) : GameState(accessCode)

    /**
     * State representing that the game has timed out
     * At this point the game is over and should be pending user action to either end or play again
     */
    class TimedOut(
        accessCode: String,
        val players: List<Player>,
        val firstPlayer: Player,
        val location: String,
    ) : GameState(accessCode)


    /**
     * State representing that the game is in an unknown state
     * This should never happen and is a catch all for any unexpected state
     */
    class Unknown(accessCode: String, ) : GameState(accessCode)
}