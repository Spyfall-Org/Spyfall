package com.dangerfield.libraries.game

// TODO cleanup this didnt end up becoming super helpful, lets reevaluate
sealed class GameDataSourcError(message: String) : Throwable(message) {
    data class IncompatibleVersion(
        val isCurrentLower: Boolean,
        val current: Int,
        val other: Int
    ) : GameDataSourcError(
        "Current Game Version $current it not compatible with received game version $other"
    )

    class GameNotFound(val accessCode: String) : GameDataSourcError("Game not found. Access code: $accessCode")

    data object TriedToLeaveStartedGameDataSourc : GameDataSourcError("Tried to leave a game that has already started")
}
