package com.dangerfield.libraries.game.internal

import android.util.Log
import com.dangerfield.libraries.game.Game.State
import com.dangerfield.libraries.game.GameConfig
import java.time.Clock
import javax.inject.Inject

class GetGameState @Inject constructor(
    private val clock: Clock,
    private val gameConfig: GameConfig,
) {
    operator fun invoke(
        startedAt: Long?,
        timeLimitSeconds: Int,
        elapsedSeconds: Int,
        isBeingStarted: Boolean,
        lastActiveAt: Long?,
        hasEveryoneVoted: Boolean,
    ): State {

        val forceShortGamesEnabled = gameConfig.forceShortGames
        val realTimeLimitSeconds = if (forceShortGamesEnabled) 10 else timeLimitSeconds

        return when {

            isExpired(lastActiveAt) -> State.Expired

            startedAt == null && !isBeingStarted -> State.Waiting

            startedAt == null && isBeingStarted -> State.Starting

            elapsedSeconds in 0..< realTimeLimitSeconds -> State.Started(elapsedSeconds)

            elapsedSeconds >= realTimeLimitSeconds && !hasEveryoneVoted -> State.Voting

            elapsedSeconds >= realTimeLimitSeconds && hasEveryoneVoted -> State.Results

            else -> State.Unknown
        }
    }

    private fun isExpired(lastActiveAt: Long?): Boolean {
        val lastActive = lastActiveAt ?: return false
        val currentTime = clock.instant()
        val minsSinceLastActivity = currentTime.minusMillis(lastActive).epochSecond / 60
        return minsSinceLastActivity >= gameConfig.gameInactivityExpirationMins
    }

}