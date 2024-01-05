package com.dangerfield.libraries.config

import kotlinx.coroutines.flow.Flow

/**
 * Repository to manage fetching and exposing the app config
 */
interface AppConfigRepository {

    /**
     * Exposes the most recent app config
     */
    fun config(): AppConfigMap

    /**
     * Exposes the app config stream set to update on a cadence
     */
    fun configStream(): Flow<AppConfigMap>
}
