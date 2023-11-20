package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.AppConfigMap
import spyfallx.core.Try

interface ConfigDataSource {
    suspend fun getConfig(): Try<AppConfigMap>
}
