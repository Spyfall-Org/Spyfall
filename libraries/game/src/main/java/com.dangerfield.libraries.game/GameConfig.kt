package com.dangerfield.libraries.game

interface GameConfig {
    val accessCodeLength: Int
    val minNameLength: Int
    val maxNameLength: Int
    val maxPlayers: Int
}