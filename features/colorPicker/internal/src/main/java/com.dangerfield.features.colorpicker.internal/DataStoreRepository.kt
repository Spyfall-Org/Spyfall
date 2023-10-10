package com.dangerfield.features.colorpicker.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class DataStoreRepository(
    private val appScope: CoroutineScope
) {

    fun <E> DataStore<Preferences>.setValue(value: E, toString: (E) -> String, key: String) {
        appScope.launch {
            edit {
                it[stringPreferencesKey(key)] = toString(value)
            }
        }
    }

    fun <E> DataStore<Preferences>.getValue(
        defaultValue: E,
        fromString: (String) -> E,
        key: String
    ): Flow<E> = data
        .map {
            it[stringPreferencesKey(key)]?.let { string ->
                fromString(string)
            } ?: defaultValue
        }
}
