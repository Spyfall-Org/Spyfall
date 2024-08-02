@file:Suppress("MatchingDeclarationName")

package com.dangerfield.libraries.game

sealed class GameError(val game: Game? = null, message: String? = null) : IllegalStateException(
    message + game?.let { " for game: \n$game" }
) {
    class GameAlreadyStartedError : GameError(message = "Game already started")

    class PackNotFoundError : GameError(message = "Pack not found")

    data class IncompatibleVersion(
        val isCurrentLower: Boolean,
        val current: Int,
        val other: Int
    ) : GameError(
        message = "Current Game Version $current it not compatible with received game version $other"
    )

    class CouldNotConnect(val accessCode: String) : GameError( message = "Could not connect to backend. Access code: $accessCode")

    class GameNotFound(val accessCode: String) : GameError( message = "Game not found. Access code: $accessCode")

    class TriedToLeaveStartedGame : GameError( message = "Tried to leave a game that has already started")
}