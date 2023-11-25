package com.dangerfield.libraries.game

sealed class GameError(message: String) : Throwable(message) {
    data class IncompatibleVersion(
        val isCurrentLower: Boolean,
        val current: Int,
        val other: Int
    ) : GameError(
        "Current Game Version $current it not compatible with received game version $other"
    )

    data object GameNotFound : GameError("Game not found")
}
