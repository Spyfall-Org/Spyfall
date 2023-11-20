package com.dangerfield.spyfall.startup

import com.dangerfield.libraries.config.AppConfigMap
import javax.inject.Inject

class IsLegacyBuild @Inject constructor(
    private val appConfigMapMap: AppConfigMap
) {
    operator fun invoke(): Boolean {
        return appConfigMapMap.value("is_legacy_build") ?: true
    }
}