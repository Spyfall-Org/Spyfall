package com.dangerfield.libraries.storage.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.squareup.moshi.JsonAdapter
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("TooGenericExceptionCaught")
class DataStoreJsonSerializer<T>(
    override val defaultValue: T,
    private val provideJsonAdapter: () -> JsonAdapter<T>,
) : Serializer<T> {

    override suspend fun readFrom(input: InputStream): T {
        return try {
            input.source().buffer().use {
                checkNotNull(provideJsonAdapter().fromJson(it))
            }
        } catch (e: Exception) {
            Timber.e(e)
            throw CorruptionException("Unable to read datastore", e)
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        try {
            output.sink().buffer().use {
                provideJsonAdapter().toJson(it, t)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}