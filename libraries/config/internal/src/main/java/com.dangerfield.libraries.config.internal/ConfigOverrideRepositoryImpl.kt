package com.dangerfield.libraries.config.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.config.ConfigOverride
import com.dangerfield.libraries.config.ConfigOverrideRepository
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.storage.datastore.withDistinctKeyFlow
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import oddoneout.core.Try
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.readJson
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class ConfigOverrideRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ConfigOverrideRepository {

    private val overridesFlow = dataStore
        .withDistinctKeyFlow(ConfigOverrideKey) { cachedOverrides ->
            deserializeConfigOverrides(cachedOverrides).getOrElse { emptySet() }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    override fun getOverrides(): List<ConfigOverride<Any>> = overridesFlow.value.toList()

    override fun getOverridesFlow(): Flow<List<ConfigOverride<Any>>> = overridesFlow.map {
        it.toList()
    }

    override suspend fun addOverride(override: ConfigOverride<Any>) {
        dataStore.updateData { preferences ->
            val currentJson = preferences[ConfigOverrideKey] ?: "[]"
            val currentOverrides = deserializeConfigOverrides(currentJson).getOrElse { emptyList() }

            val updatedOverrides = currentOverrides + override
            val updatedJson = serializeConfigOverrides(updatedOverrides)

            preferences.toMutablePreferences().apply {
                set(ConfigOverrideKey, updatedJson)
            }
        }
    }

    private fun deserializeConfigOverrides(json: String?): Try<List<ConfigOverride<Any>>> = Try {
        json?.let { moshi.readJson<List<ConfigOverride<Any>>>(it) } ?: emptyList()
    }

    private fun serializeConfigOverrides(overrides: List<ConfigOverride<Any>>): String {
        val elementType = Types.newParameterizedType(ConfigOverride::class.java, Any::class.java)
        val listType = Types.newParameterizedType(List::class.java, elementType)
        val jsonAdapter = moshi.adapter<List<ConfigOverride<Any>>>(listType)
        return jsonAdapter.toJson(overrides)
    }

    companion object {
        private val ConfigOverrideKey = stringPreferencesKey("config_overrides")
    }
}