package com.dangerfield.features.waitingroom.internal

import com.dangerfield.libraries.game.GameError.TriedToLeaveStartedGame
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.UpdateActiveGame
import spyfallx.core.Try
import spyfallx.core.developerSnackOnError
import spyfallx.core.failure
import spyfallx.core.logOnError
import javax.inject.Inject

class LeaveGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val clearActiveGame: ClearActiveGame
) {
    suspend operator fun invoke(
        accessCode: String,
        id: String,
        isGameBeingStarted: Boolean
    ): Try<Unit> =
        if (isGameBeingStarted) {
            TriedToLeaveStartedGame.failure()
        } else {
            gameRepository
                .removeUser(accessCode, id)
                .onSuccess { clearActiveGame() }
                .logOnError()
                .developerSnackOnError { "Error leaving game" }
        }
}