package com.dangerfield.libraries.game

data class Player(

    /**
     * Unique identifier for the player
     */
    val id: String,

    /**
     * The role for the player durring game play
     */
    val role: String?,

    /**
     * The name of the player (required to be unique)
     */
    val userName: String,

    /**
     * Flag for if the player is the host of the game
     * Hosts are the only ones that can boot other players
     */
    val isHost: Boolean,

    /**
     * Flag for if the player is the odd one out
     * Easier than checking the role due to localization
     */
    val isOddOneOut: Boolean,

    /**
     * Weather or not the player has voted correctly
     * (null if the player has not voted yet)
     * used to determine when voting has ended and which side won
     */
    val votedCorrectly: Boolean?
) {

    fun hasVoted(): Boolean = votedCorrectly != null
    fun hasRole(): Boolean = role != null
    fun votedCorrectly() = votedCorrectly == true
}