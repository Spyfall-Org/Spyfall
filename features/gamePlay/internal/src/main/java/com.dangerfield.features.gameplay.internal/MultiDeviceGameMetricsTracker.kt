package com.dangerfield.features.gameplay.internal

import androidx.core.os.bundleOf
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.ACCESS_CODE
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.DRAW
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.ERROR
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.GAME_ENDED
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.GAME_RESTARTED
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.GAME_RESTART_ERROR
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.GAME_TYPE
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.LOCATION
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.ODD_ONE_OUT
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.PLAYERS
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.PLAYER_COUNT
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.RESULT
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.TIME_LIMIT_MINS
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.TIME_REMAINING
import com.dangerfield.features.gameplay.internal.metrics.MetricConstants.VOTING_ENDED
import com.dangerfield.libraries.analytics.Metric
import com.dangerfield.libraries.analytics.MetricsTracker
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.GameState
import javax.inject.Inject

class MultiDeviceGameMetricsTracker @Inject constructor(
    private val metricsTracker: MetricsTracker
) {

    fun trackGameStarted(
        game: Game,
        ) {
        metricsTracker.log(
            Metric.Event.Custom(
                MetricConstants.GAME_STARTED,
                bundleOf(
                    GAME_TYPE to MULTI_DEVICE_GAME,
                    ACCESS_CODE to game.accessCode,
                    TIME_LIMIT_MINS to game.timeLimitMins,
                    PLAYER_COUNT to game.players.size,
                    LOCATION to game.locationName,
                )
            )
        )
    }

    fun trackGameStartFailure(
        game: Game,
        throwable: Throwable
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                MetricConstants.GAME_START_ERROR,
                bundleOf(
                    GAME_TYPE to MULTI_DEVICE_GAME,
                    ACCESS_CODE to game.accessCode,
                    TIME_LIMIT_MINS to game.timeLimitMins,
                    PLAYER_COUNT to game.players.size,
                    LOCATION to game.locationName,
                    MetricConstants.ERROR_MESSAGE to throwable.message
                )
            )
        )
    }

    fun trackGameEnded(
        game: Game,
        timeRemainingMillis: Long
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                GAME_ENDED,
                bundleOf(
                    GAME_TYPE to MULTI_DEVICE_GAME,
                    TIME_REMAINING to timeRemainingMillis,
                    ACCESS_CODE to game.accessCode,
                    TIME_LIMIT_MINS to game.timeLimitMins,
                    PLAYER_COUNT to game.players.size,
                    LOCATION to game.locationName,
                )
            )
        )
    }

    fun trackGameRestarted(
        game: Game,
        timeRemainingMillis: Long
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                GAME_RESTARTED,
                bundleOf(
                    GAME_TYPE to MULTI_DEVICE_GAME,
                    TIME_REMAINING to timeRemainingMillis,
                    ACCESS_CODE to game.accessCode,
                    TIME_LIMIT_MINS to game.timeLimitMins,
                    PLAYER_COUNT to game.players.size,
                    LOCATION to game.locationName,
                )
            )
        )
    }

    fun trackGameRestartError(
        game: Game,
        timeRemainingMillis: Long
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                GAME_RESTART_ERROR,
                bundleOf(
                    GAME_TYPE to MULTI_DEVICE_GAME,
                    TIME_REMAINING to timeRemainingMillis,
                    ACCESS_CODE to game.accessCode,
                    TIME_LIMIT_MINS to game.timeLimitMins,
                    PLAYER_COUNT to game.players.size,
                    LOCATION to game.locationName,
                )
            )
        )
    }

    fun trackVotingEnded(
        timeLimitMins: Int,
        gameState: GameState.VotingEnded
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                VOTING_ENDED,
                bundleOf(
                    GAME_TYPE to MULTI_DEVICE_GAME,
                    ACCESS_CODE to gameState.accessCode,
                    TIME_LIMIT_MINS to timeLimitMins,
                    PLAYER_COUNT to gameState.players.size,
                    LOCATION to gameState.location,
                    RESULT to when (gameState.result) {
                        GameResult.PlayersWon -> PLAYERS
                        GameResult.OddOneOutWon -> ODD_ONE_OUT
                        GameResult.Draw -> DRAW
                        GameResult.Error -> ERROR
                    }
                )
            )
        )
    }

    companion object {
        private const val MULTI_DEVICE_GAME = "multi_device_game"
    }
}