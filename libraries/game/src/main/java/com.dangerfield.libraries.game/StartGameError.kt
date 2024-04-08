package com.dangerfield.libraries.game

//TODO cleanup
sealed class StartGameError(message: String) : Throwable(message) {
    data object GameDataSourcAlreadyStarted : GameDataSourcError("Game already started")
}
