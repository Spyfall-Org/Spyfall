package com.dangerfield.libraries.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import oddoneout.core.Try

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

suspend fun DataStore<Preferences>.cache(key: Preferences.Key<String>, value: String) {
    updateData {
        it.toMutablePreferences()
            .apply {
                this[key] = value
            }
    }
}

suspend fun <T> DataStore<Preferences>.getValue(
    key: Preferences.Key<String>,
    default: T? = null,
    fromString: (String) -> T?,
): T? {
    return distinctKeyFlow(key)
        .map { cachedValue ->
            cachedValue?.let { string ->
                Try { fromString(string) }
                    .getOrNull()
                    ?: default
            } ?: default
        }
        .firstOrNull()
}

fun <T> DataStore<Preferences>.getValueFlow(
    key: Preferences.Key<String>,
    default: T? = null,
    fromString: (String) -> T?,
): Flow<T?> {
    return distinctKeyFlow(key)
        .map { cachedValue ->
            cachedValue?.let { string ->
                Try { fromString(string) }
                    .getOrNull()
                    ?: default
            } ?: default
        }
}



