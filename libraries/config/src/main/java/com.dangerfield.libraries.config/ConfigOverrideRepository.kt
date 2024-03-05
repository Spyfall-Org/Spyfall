package com.dangerfield.libraries.config

import kotlinx.coroutines.flow.Flow

interface ConfigOverrideRepository {
    fun getOverrides(): List<ConfigOverride<Any>>
    fun getOverridesFlow(): Flow<List<ConfigOverride<Any>>>
    suspend fun addOverride(override: ConfigOverride<Any>)
}