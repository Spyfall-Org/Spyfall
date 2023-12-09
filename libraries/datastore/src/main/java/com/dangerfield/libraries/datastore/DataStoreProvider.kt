package com.dangerfield.libraries.datastore

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import java.io.File

interface DataStoreProvider {
    fun <T> create(
        serializer: Serializer<T>,
        corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
        migrations: List<DataMigration<T>> = listOf(),
        produceFile: () -> File,
    ): DataStore<T>
}
