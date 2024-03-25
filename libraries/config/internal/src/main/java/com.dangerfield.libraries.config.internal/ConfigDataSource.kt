package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.AppConfigMap
import oddoneout.core.Catching

interface ConfigDataSource {
    suspend fun getConfig(): Catching<AppConfigMap>
}
