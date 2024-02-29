package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.AppConfigMap
import kotlinx.coroutines.flow.Flow
import oddoneout.core.Try

/**
 * Abstraction to encapsulate the logic behind the local storage of the app config.
 *
 */
interface CachedConfigDataSource {
    /**
     * @return a flow that emits a value for every update to the local app config
     */
    fun getConfigFlow(): Flow<AppConfigMap>

    suspend fun getConfig(): Try<AppConfigMap>

    /**
     * updates the locally stored app config
     */
    suspend fun updateConfig(config: AppConfigMap)
}
