package com.dangerfield.libraries.config

import kotlinx.coroutines.flow.Flow

/**
 * A repository for managing the values that should take precedence over the configured values.
 * If an override is present for a given key, the value from the override will be used instead of the
 * configured value.
 */
interface ConfigOverrideRepository {
    fun getOverrides(): List<ConfigOverride<Any>>
    fun getOverridesFlow(): Flow<List<ConfigOverride<Any>>>
    suspend fun addOverride(override: ConfigOverride<Any>)
}