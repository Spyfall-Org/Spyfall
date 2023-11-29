package com.dangerfield.libraries.game

/**
 * WARNING: This version should be update
 */
const val CURRENT_GAME_MODEL_VERSION = 1
data class Game(

    /**
     * The unique identifier for the game
     * used to join the game
     */
    val accessCode: String,

    /**
     * This location is randomly chosen from the list of locations of the chosen packs
     * It is assigned by the creator of the game
     */
    val location: String,

    /**
     * A list of names (used as keys) of the packs chosen by the creator of the game
     * this is used to get the list of locations for the game
     */
    val packNames: List<String>,

    /**
     * A boolean indicating that the game has been started
     * This is flipped by whoever starts the game to prevent other players from joining
     * or starting themselves
     */
    val hasStarted: Boolean,


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
    val locations: List<String>,

    /**
     * Optional Link that the creator of the game can enter to be shared with players in the waiting
     * room.
     */
    val videoCallLink: String?,

    /**
     * The version of the game being played. Older versions of the app may not be able to play with newer versions
     * if the game model changes. So we track the model being used with a version number
     */
    val version: Int
)
