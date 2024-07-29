package com.dangerfield.libraries.game

interface GameConfig {
    val accessCodeLength: Int
    val minNameLength: Int
    val maxNameLength: Int
    val maxPlayers: Int
    val minPlayers: Int
    val maxTimeLimit: Int
    val minTimeLimit: Int
    val itemsPerGame: Int
    val isSingleDeviceModeEnabled: Boolean
    val isVideoCallLinkEnabled: Boolean
    val forceShortGames: Boolean
    val itemsPerSingleDeviceGame: Int
    val gameInactivityExpirationMins: Int
    val canNonHostsControlGame: Boolean
    val packsVersion: Int
}