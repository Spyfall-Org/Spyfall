package com.dangerfield.features.forcedupdate.internal

import android.util.Log
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.BuildInfo
import javax.inject.Inject

@AutoBind
class DefaultIsUpdateRequired @Inject constructor(
    private val buildInfo: BuildInfo,
    private val appUpdateDataSource: AppUpdateDataSource
) : IsAppUpdateRequired {

    override suspend operator fun invoke(): Boolean = appUpdateDataSource
        .getMinimumVersionCode()?.let {
            Log.d("Elijah", "buildInfo.versionCode: ${buildInfo.versionCode}, min req code: $it")
            Log.d("Elijah", " buildInfo.versionCode < min required code: ${buildInfo.versionCode < it}")
            buildInfo.versionCode < it
        } ?: false
}
