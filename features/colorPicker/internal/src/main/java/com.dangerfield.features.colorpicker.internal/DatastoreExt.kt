package com.dangerfield.features.colorpicker.internal

import androidx.datastore.core.DataStore
import com.dangerfield.libraries.coreflowroutines.runCancellableCatching

suspend fun <T> DataStore<T>.tryUpdateData(transform: suspend (t: T) -> T): Result<T> = runCancellableCatching {
    updateData { transform(it) }
}
