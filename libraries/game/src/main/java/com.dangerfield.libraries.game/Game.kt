package com.dangerfield.libraries.game

/**
 * WARNING: This version should be update if the core model changes. It is used to tell if 2
 * versions of the app are compatible to play together
 */
const val CURRENT_GAME_MODEL_VERSION = 1
data class Game(

    /**
     * The unique identifier for the game
     * used to join the game
     */
    val accessCode: String,

    /**
     * This location key that is randomly chosen from the list of locations of the chosen packs
     * It is assigned by the creator of the game
     */
    val locationName: String,

    /**
     * A list of names (used as keys) of the packs chosen by the creator of the game
     * this is used to get the list of locations for the game
     */
    val packNames: List<String>,

    /**
     * A boolean indicating that the game is being started
     * This is flipped by whoever starts the game to prevent other players from joining
     * or starting themselves, this does NOT indicate that the game is in progress
     *
     * The game is considered in progress when all roles are assigned and the startedAt field is not null
     */
    val isBeingStarted: Boolean,

    /**
     * A list of player objects that contains information needed for gameplay
     * The roles for these object are assigned by the person that starts the game
     * When everyone has roles the game can be officially started
     */
    val players: List<Player>,

    /**
     * Time limit of the game in mins
     */
    val timeLimitMins: Int,

    /**
     * The epoch in millis of when the game was started
     * This is used as a source of truth or elapsed time during game play
     */
    val startedAt: Long?,

    /**
     * A list of locations that are randomly chosen from packs of the game
     * This is created by the creator of the game
     */
    val locationOptionNames: List<String>,

    /**
     * Optional Link that the creator of the game can enter to be shared with players in the waiting
     * room.
     */
    val videoCallLink: String?,

    /**
     * The version of the game being played. Older versions of the app may not be able to play with newer versions
     * if the game model changes. So we track the model being used with a version number
     */
    val version: Int,

    /**
     * The timestamp of the last activity in the game
     * Activity defined as any change to the game model
     * player joining, game starting, game restarting, etc.
     */
    val lastActiveAt: Long?
) {
    fun player(id: String?): Player? = players.find { it.id == id }
}
