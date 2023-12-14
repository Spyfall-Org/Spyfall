package com.dangerfield.libraries.game

sealed class StartGameError(message: String) : Throwable(message) {

    data object GameAlreadyStarted : GameError("Game already started")
}
