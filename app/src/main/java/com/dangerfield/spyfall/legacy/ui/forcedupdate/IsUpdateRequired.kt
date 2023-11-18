package com.dangerfield.spyfall.legacy.ui.forcedupdate

import android.util.Log
import spyfallx.core.BuildInfo
import javax.inject.Inject

class IsUpdateRequired @Inject constructor(
    private val buildInfo: BuildInfo,
    private val appUpdateDataSource: AppUpdateDataSource
) {

    suspend operator fun invoke(): Boolean = appUpdateDataSource.getMinimumVersionCode()?.let {
        Log.d("Elijah", "buildInfo.versionCode: ${buildInfo.versionCode}, min req code: $it")
        Log.d("Elijah", " buildInfo.versionCode < min required code: ${buildInfo.versionCode < it}")
        buildInfo.versionCode < it
    } ?: false
}
