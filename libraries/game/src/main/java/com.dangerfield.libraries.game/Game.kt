package com.dangerfield.libraries.game

import java.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class Game(

    /**
     * The unique identifier for the game. Used to join the game
     */
    val accessCode: String,

    /**
     * The secret that the odd one out is trying to guess
     */
    val secretItem: PackItem,

    /**
     * The packs that are being used for the game. Chosen when creating
     */
    val packs: List<Pack<PackItem>>,

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
    val packsVersion: Int,

    /**
     * The state of the game
     */
    val state: State,

    /**
     * The player object of the current user
     */
    val mePlayer: Player?

) {
    fun player(id: String?): Player? = players.find { it.id == id }

    fun hasEveryoneVoted(): Boolean = players.all { it.votedCorrectly != null }

    fun remainingTimeMillis(clock: Clock): Long {
        val startedAtMillis = startedAt ?: return timeLimitMins.minutes.inWholeMilliseconds
        val timeLimitInMillis = if (timeLimitMins == -1) {
            10.seconds.inWholeMilliseconds
        } else {
            timeLimitMins.minutes.inWholeMilliseconds
        }

        val elapsedMillis: Long = clock.millis() - startedAtMillis
        val remainingMillis = timeLimitInMillis - elapsedMillis
        return remainingMillis
    }

    val result: GameResult
     get() {
        val oddOneOut = this.players.find { it.isOddOneOut } ?: return GameResult.Error
        val nonOddPLayers = (this.players - oddOneOut)
        val majorityPlayersVotedCorrectly = nonOddPLayers.count { it.votedCorrectly() } > nonOddPLayers.size / 2

        return when {
            !players.everyoneHasVoted() -> GameResult.None
            majorityPlayersVotedCorrectly && oddOneOut.votedCorrectly() -> GameResult.Draw
            majorityPlayersVotedCorrectly -> GameResult.PlayersWon
            oddOneOut.votedCorrectly() -> GameResult.OddOneOutWon
            else -> GameResult.Draw
        }
    }

    sealed class State {
        data object Waiting: State()
        data object Starting: State()
        data object Started: State()
        data object Voting: State()
        data object Results: State()
        data object Expired: State()
        data object Unknown: State()
    }
}

private fun Collection<Player>.everyoneHasVoted() = this.all { it.votedCorrectly != null }


