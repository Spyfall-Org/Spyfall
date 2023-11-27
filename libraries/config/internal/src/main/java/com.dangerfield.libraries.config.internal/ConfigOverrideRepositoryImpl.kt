package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.ConfigOverride
import com.dangerfield.libraries.config.ConfigOverrideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class ConfigOverrideRepositoryImpl @Inject constructor() :
    ConfigOverrideRepository {
    private val overridesFlow = MutableStateFlow(emptySet<ConfigOverride<Any>>())

    override fun getOverrides(): List<ConfigOverride<Any>> = overridesFlow.value.toList()

    override fun getOverridesFlow(): Flow<List<ConfigOverride<Any>>> = overridesFlow.map {
        it.toList()
    }

    override fun addOverride(override: ConfigOverride<Any>) {
        overridesFlow.update {
            it + override
        }
    }
}