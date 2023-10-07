package com.dangerfield.spyfall.legacy.ui.forcedupdate

import spyfallx.core.BuildInfo
import javax.inject.Inject

class IsUpdateRequired @Inject constructor(
    private val buildInfo: BuildInfo,
    private val appUpdateDataSource: AppUpdateDataSource
) {

    suspend operator fun invoke(): Boolean = appUpdateDataSource.getMinimumVersionCode()?.let {
        buildInfo.versionCode < it
    } ?: false
}
