package com.dangerfield.libraries.config.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.config.AppConfigMap
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import spyfallx.core.logOnError
import timber.log.Timber
import javax.inject.Inject

private const val ConfigKey = "config"

/**
 * data store implementation of [CachedConfigDataSource]
 */
@AutoBind
class DataStoreConfigDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    moshi: Moshi
) : CachedConfigDataSource {

    private val jsonAdapter = moshi.adapter<Map<String, *>>(
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    )

    override fun getConfigFlow(): Flow<AppConfigMap> = dataStore.data
        .mapNotNull { preferences ->
            preferences.getConfigOrNull()
        }

    override suspend fun getConfig(): Try<AppConfigMap> = Try {
        checkNotNull( dataStore.data.first().getConfigOrNull())
    }

    override suspend fun updateConfig(config: AppConfigMap) {
        val jsonString = jsonAdapter.toJson(config.map)
        Timber.d("Updating config in data store to: \n $jsonString")
        dataStore.edit {
            it[stringPreferencesKey(ConfigKey)] = jsonString
        }
    }

    private fun Preferences.getConfigOrNull(): AppConfigMap? {
        val storedConfigString = this[stringPreferencesKey(ConfigKey)]

        return if (storedConfigString == null) {
            Timber.d("Stored config was null")
            null
        } else {
            Try {
                val map = jsonAdapter.fromJson(storedConfigString)
                checkNotNull(map) { "Map parsed to null: \n $storedConfigString" }
                Timber.d("Emitting config from data store: \n $storedConfigString")
                BasicAppConfigMapMap(map)
            }
                .logOnError()
                .getOrNull()
        }

    }
}
