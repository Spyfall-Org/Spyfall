package com.dangerfield.features.forcedupdate.internal

import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import com.dangerfield.libraries.config.AppConfigFlow
import com.dangerfield.libraries.config.AppConfigMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.BuildInfo
import timber.log.Timber
import javax.inject.Inject

@AutoBind
class DefaultIsUpdateRequired @Inject constructor(
    private val buildInfo: BuildInfo,
    private val minVersionCodeValue: MinVersionCode,
    private val appConfigFlow: AppConfigFlow
) : IsAppUpdateRequired {

    override suspend operator fun invoke(): Flow<Boolean> =
        appConfigFlow.map {
            val minVersionCode = it.value(minVersionCodeValue)
            Timber.d("Min Version Code Received: $minVersionCodeValue() | build version: ${buildInfo.versionCode} | Is update required: $it")
            buildInfo.versionCode < minVersionCode
        }
}
