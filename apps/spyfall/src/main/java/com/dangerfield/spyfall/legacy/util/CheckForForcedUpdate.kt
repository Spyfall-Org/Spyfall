package com.dangerfield.spyfall.legacy.util

import android.util.Log
import com.dangerfield.spyfall.legacy.api.GameService
import spyfallx.core.BuildInfo

class CheckForForcedUpdate(
    private val fireStoreService: GameService,
    private val buildInfo: BuildInfo
) {

    suspend fun shouldRequireUpdate(): Boolean {
        return fireStoreService.getRequiredVersionCode()?.let { requiredCode ->
            requiredCode > buildInfo.versionCode
        } ?: false
    }
}
