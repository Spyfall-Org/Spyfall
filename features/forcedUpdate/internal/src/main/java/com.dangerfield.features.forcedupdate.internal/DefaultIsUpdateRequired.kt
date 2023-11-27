package com.dangerfield.features.forcedupdate.internal

import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.BuildInfo
import timber.log.Timber
import javax.inject.Inject

@AutoBind
class DefaultIsUpdateRequired @Inject constructor(
    private val buildInfo: BuildInfo,
    private val minVersionCodeValue: MinVersionCode
) : IsAppUpdateRequired {

    private val minVersionCode: Int
        get() = minVersionCodeValue.value

    override suspend operator fun invoke(): Boolean = (buildInfo.versionCode < minVersionCode)
        .also {
            Timber.d("Min Version Code Received: $minVersionCode | build version: ${buildInfo.versionCode} | Is update required: $it")
        }
}
