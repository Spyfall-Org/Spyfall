package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.AppConfigMap
import oddoneout.core.Try

interface ConfigDataSource {
    suspend fun getConfig(): Try<AppConfigMap>
}
