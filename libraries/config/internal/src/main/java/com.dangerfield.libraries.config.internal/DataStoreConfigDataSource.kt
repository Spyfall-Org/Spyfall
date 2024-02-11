package com.dangerfield.libraries.config.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.internal.model.BasicMapBasedAppConfigMapMap
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.storage.datastore.withDistinctKeyFlow
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.Try
import oddoneout.core.logOnError
import timber.log.Timber
import javax.inject.Inject

private val ConfigKey = stringPreferencesKey("config")

/**
 * data store implementation of [CachedConfigDataSource]
 */
@AutoBind
class DataStoreConfigDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val applicationScope: CoroutineScope,
    moshi: Moshi
) : CachedConfigDataSource {

    private val jsonAdapter = moshi.adapter<Map<String, *>>(
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    )

    private val configFlow = dataStore.withDistinctKeyFlow(ConfigKey) { storedConfigString ->
        deserializeConfig(storedConfigString)
    }
        .filterNotNull()
        .shareIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    private fun deserializeConfig(storedConfigString: String?) = Try {
        val map = storedConfigString?.let { jsonAdapter.fromJson(it) } ?: emptyMap<String, Any>()
        Timber.d("Emitting config from data store: \n $storedConfigString")
        BasicMapBasedAppConfigMapMap(map)
    }
        .logOnError()
        .getOrNull()

    override fun getConfigFlow(): Flow<AppConfigMap> = configFlow

    override suspend fun getConfig(): Try<AppConfigMap> = Try {
        configFlow.replayCache.firstOrNull() ?: configFlow.first()
    }

    override suspend fun updateConfig(config: AppConfigMap) {
        val jsonString = jsonAdapter.toJson(config.map)
        Timber.d("Updating config in data store to: \n $jsonString")
        dataStore.edit {
            it[ConfigKey] = jsonString
        }
    }
}
