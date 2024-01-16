package com.dangerfield.features.waitingroom.internal

import androidx.core.os.bundleOf
import com.dangerfield.libraries.analytics.Metric
import com.dangerfield.libraries.analytics.MetricsTracker
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError.TriedToLeaveStartedGame
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.session.ClearActiveGame
import oddoneout.core.Try
import oddoneout.core.developerSnackOnError
import oddoneout.core.failure
import oddoneout.core.logOnError
import javax.inject.Inject
import javax.inject.Named

class LeaveGameUseCase @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val clearActiveGame: ClearActiveGame,
    private val metricsTracker: MetricsTracker
) {
    suspend operator fun invoke(
        game: Game,
        id: String,
        isGameBeingStarted: Boolean
    ): Try<Unit> {
        val playerLeaving = game.players.firstOrNull { it.id == id }
        return if (isGameBeingStarted) {
            TriedToLeaveStartedGame.failure()
        } else {
            gameRepository
                .removeUser(game.accessCode, id)
                .onSuccess {
                    metricsTracker.log(
                        Metric.Event.Custom(
                            eventName = "player_left_game",
                            extras = bundleOf(
                                "access_code" to game.accessCode,
                                "user_id" to id,
                                "user_name" to playerLeaving?.userName,
                                "is_host" to playerLeaving?.isHost,
                                "playerCount" to game.players.size,
                                "version" to game.version
                            )
                        )
                    )
                    clearActiveGame()
                }
                .logOnError()
                .developerSnackOnError { "Error leaving game" }
        }
    }
}