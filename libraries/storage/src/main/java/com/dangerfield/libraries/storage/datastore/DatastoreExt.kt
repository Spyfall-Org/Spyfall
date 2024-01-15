package com.dangerfield.libraries.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import spyfallx.core.Try

suspend fun <T> DataStore<T>.tryUpdateData(transform: suspend (t: T) -> T): Try<T> = Try {
    updateData { transform(it) }
}

fun DataStore<Preferences>.distinctKeyFlow(key: Preferences.Key<String>) = data
    .map { it[key] }
    .distinctUntilChanged()

fun <R> DataStore<Preferences>.withDistinctKeyFlow(
    key: Preferences.Key<String>,
    transform: suspend (value: String?) -> R
) = data
    .map { it[key] }
    .distinctUntilChanged()
    .map(transform)
