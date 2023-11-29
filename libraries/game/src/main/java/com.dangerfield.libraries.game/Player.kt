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
     * Flag for if the player is the spy
     * Easier than checking the role due to localization
     */
    val isSpy: Boolean
)