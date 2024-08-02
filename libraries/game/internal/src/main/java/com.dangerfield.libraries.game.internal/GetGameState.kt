package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.Game.State
import com.dangerfield.libraries.game.GameConfig
import java.time.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class GetGameState @Inject constructor(
    private val clock: Clock,
    private val gameConfig: GameConfig,
) {
    operator fun invoke(game: Game): State {

        val startedAt = game.startedAt

        return when {

            isExpired(game) -> State.Expired

            startedAt == null && !game.isBeingStarted
            -> State.Waiting

            startedAt == null && game.isBeingStarted
            -> State.Starting

            startedAt != null && remainingMillis(startedAt, game.timeLimitMins) > 0
            -> State.Started

            startedAt != null && remainingMillis(startedAt, game.timeLimitMins) <= 0 && !game.hasEveryoneVoted()
            -> State.Voting

            startedAt != null
                    && remainingMillis(startedAt, game.timeLimitMins) <= 0
                    && game.hasEveryoneVoted()
            -> State.Results

            else -> State.Unknown
        }
    }

    private fun remainingMillis(startedAtMillis: Long, timeLimitMins: Int): Long {
        // -1 is used in debug builds to simulate a short time limit
        val timeLimitInMillis = if (timeLimitMins == -1) {
            10.seconds.inWholeMilliseconds
        } else {
            timeLimitMins.minutes.inWholeMilliseconds
        }

        val elapsedMillis: Long = clock.millis() - startedAtMillis
        val remainingMillis = timeLimitInMillis - elapsedMillis
        return remainingMillis
    }

    private fun isExpired(game: Game): Boolean {
        val lastActive = game.lastActiveAt ?: return false
        val currentTime = clock.instant()
        val minsSinceLastActivity = currentTime.minusMillis(lastActive).epochSecond / 60
        return minsSinceLastActivity >= gameConfig.gameInactivityExpirationMins
    }

}