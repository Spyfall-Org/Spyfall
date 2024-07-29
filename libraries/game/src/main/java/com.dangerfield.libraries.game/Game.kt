package com.dangerfield.libraries.game

import com.squareup.moshi.JsonClass

/**
 * WARNING: This version should be update if the core model changes. It is used to tell if 2
 * versions of the app are compatible to play together
 */
const val CURRENT_GAME_MODEL_VERSION = 2

/**
 * The version of packs a build should use is determined by the config. If the config fails and
 * there is no cache we will fallback to this value
 */
const val CURRENT_PACKS_VERSION_FALLBACK = 1

//TODO i should probably use the versioned moshi adapter for this
@JsonClass(generateAdapter = true)
data class Game(

    /**
     * The unique identifier for the game
     * used to join the game
     */
    val accessCode: String,

    /**
     * The secret that the odd one out is trying to guess
     */
    val secret: String,

    /**
     * A list of names (used as keys) of the packs chosen by the creator of the game
     * this is used to get the list of locations for the game
     *
     * TODO consider just storing the entire packs in this object
     * It would only need fetched once and would be easier to manage
     */
    val packIds: List<String>,

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
    val secretOptions: List<String>,

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
    val lastActiveAt: Long?,

    /**
     * The language used for the game. This is determined by the creator of the game
     * when another user starts the game the language set for the game is used to fetch roles.
     * When there is no language set we assume `en`
     * This is because language is a newer field. Before we added it the only language supported
     * we english. So if there is not a language we can safely assume english
     */
    val languageCode: String,


    /**
     * The version of packs being used.
     *
     * The only reason the packs version is used is if another player needs to make sure
     * they have the correct pack data related to the game.
     *
     * Which as of writing this the only
     * reason a player would need to make sure they have the correct pack data is if they are
     * starting or restarting the game and they arent the host. In that case they will be
     * taking the location, fetching the roles, and assigning the roles to the players. So they
     * need to have the updated packs.
     *
     */
    val packsVersion: Int
) {
    fun player(id: String?): Player? = players.find { it.id == id }
}
