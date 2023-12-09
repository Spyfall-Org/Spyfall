package com.dangerfield.libraries.datastore

import androidx.datastore.core.DataStore
import spyfallx.core.Try

suspend fun <T> DataStore<T>.tryUpdateData(transform: suspend (t: T) -> T): Try<T> = Try {
    updateData { transform(it) }
}
