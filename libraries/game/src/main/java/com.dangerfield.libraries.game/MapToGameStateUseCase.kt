package com.dangerfield.libraries.game

interface MapToGameStateUseCase {
    operator fun invoke(accessCode: String, game: Game?): GameState
}

