package com.dangerfield.features.colorpicker.internal

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retry
import com.dangerfield.libraries.flowroutines.mapResult
import com.dangerfield.libraries.flowroutines.runCancellableCatching
import com.dangerfield.libraries.flowroutines.withRetry

interface CatchingDataStore<T> {
    val data: Flow<Result<T>>
    suspend fun updateData(transform: suspend (t: T) -> T): Result<T>
}

fun <T> DataStore<T>.asCatching(): CatchingDataStore<T> = object : CatchingDataStore<T> {
    override val data: Flow<Result<T>> =
        this@asCatching.data
            .retry(1)
            .mapResult()

    override suspend fun updateData(transform: suspend (t: T) -> T): Result<T> =
        withRetry(1) {
            runCancellableCatching {
                this@asCatching.updateData(transform)
            }
        }
}
