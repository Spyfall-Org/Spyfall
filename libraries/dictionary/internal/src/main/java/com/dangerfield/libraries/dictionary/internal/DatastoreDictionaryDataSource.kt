package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import oddoneout.core.Try
import oddoneout.core.logOnError
import se.ansman.dagger.auto.AutoBind
import com.dangerfield.libraries.storage.datastore.withDistinctKeyFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val DictionaryKey = stringPreferencesKey("dictionary")

@AutoBind
@Singleton
class DatastoreDictionaryDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    moshi: Moshi
) : CachedDictionaryDataSource {

    private val jsonAdapter = moshi.adapter<Map<String, String>>(
        Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    )

    private val dictionaryFlow = dataStore.withDistinctKeyFlow(DictionaryKey) { cachedDictionaryJson ->
        deserializeDictionary(cachedDictionaryJson.orEmpty())
    }
        .filterNotNull()
        .shareIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    private fun deserializeDictionary(cachedDictionaryJson: String) = Try {
        val map = jsonAdapter.fromJson(cachedDictionaryJson)
        checkNotNull(map) { "Map parsed to null: \n $cachedDictionaryJson" }
        Timber.d("Emitting dictionary from data store: \n $cachedDictionaryJson")
        OverrideDictionary(
            context = context,
            map = map
        )
    }
        .logOnError()
        .getOrNull()

    override fun getDictionaryFlow(): Flow<OverrideDictionary> = dictionaryFlow

    override suspend fun getDictionary(): Try<OverrideDictionary> = Try {
        dictionaryFlow.replayCache.firstOrNull() ?: dictionaryFlow.first()
    }

    override suspend fun updateDictionary(dictionary: OverrideDictionary) {
        val jsonString = jsonAdapter.toJson(dictionary.map)
        Timber.d("Updating dictionary in data store to: \n $jsonString")
        dataStore.edit {
            it[DictionaryKey] = jsonString
        }
    }
}
