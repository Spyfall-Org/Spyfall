package com.dangerfield.features.forcedupdate.internal

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
            buildInfo.versionCode < it
        } ?: false
}
