package com.dangerfield.libraries.game

interface GameConfig {
    val accessCodeLength: Int
    val minNameLength: Int
    val maxNameLength: Int
    val maxPlayers: Int
    val minPlayers: Int
    val maxTimeLimit: Int
    val minTimeLimit: Int
    val locationsPerGame: Int
    val isSingleDeviceModeEnabled: Boolean
    val forceShortGames: Boolean
    val gameInactivityExpirationMins: Int
}