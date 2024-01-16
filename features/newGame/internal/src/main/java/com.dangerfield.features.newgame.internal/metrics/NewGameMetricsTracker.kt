package com.dangerfield.features.newgame.internal.metrics

import android.os.Bundle
import com.dangerfield.libraries.analytics.Metric
import com.dangerfield.libraries.analytics.MetricsTracker
import javax.inject.Inject

class NewGameMetricsTracker @Inject constructor(
    private val metricsTracker: MetricsTracker
) {

    fun trackMultiDeviceGameCreated(
        accessCode: String,
        location: String,
        packs: List<String>,
        timeLimit: Int,
        videoLink: String?,
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                eventName = MULTI_DEVICE_GAME_CREATED,
                extras = Bundle().apply {
                    putString(ACCESS_CODE, accessCode)
                    putString(LOCATION, location)
                    putString(PACKS, packs.joinToString(","))
                    putInt(TIME_LIMIT, timeLimit)
                    putString(VIDEO_LINK, videoLink ?: "null")
                }
            )
        )
    }

    fun trackSingleDeviceGameCreated(
        packs: List<String>,
        timeLimit: Int,
        playerCount: Int
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                eventName = SINGLE_DEVICE_GAME_CREATED,
                extras = Bundle().apply {
                    putString(PACKS, packs.joinToString(","))
                    putInt(TIME_LIMIT, timeLimit)
                    putInt(PLAYER_COUNT, playerCount)
                }
            )
        )
    }

    fun trackErrorCreatingGame(
        isSingleDevice: Boolean,
        error: Throwable
    ) {
        metricsTracker.log(
            Metric.Event.Error(
                errorName = ERROR_CREATING_GAME,
                throwable = error,
                extras = Bundle().apply {
                    putBoolean(IS_SINGLE_DEVICE, isSingleDevice)
                }
            )
        )
    }

    companion object {
        private const val ACCESS_CODE = "access_code"
        private const val PACKS = "packs"
        private const val TIME_LIMIT = "time_limit"
        private const val VIDEO_LINK = "video_link"
        private const val LOCATION = "location"
        private const val PLAYER_COUNT = "player_count"
        private const val IS_SINGLE_DEVICE = "is_single_device"
        private const val ERROR_CREATING_GAME = "error_creating_game"
        private const val MULTI_DEVICE_GAME_CREATED = "multi_device_game_created"
        private const val SINGLE_DEVICE_GAME_CREATED = "single_device_game_created"
    }
}