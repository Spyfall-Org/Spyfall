package com.dangerfield.libraries.storage.internal

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.dangerfield.libraries.coreflowroutines.childSupervisorScope
import com.dangerfield.libraries.storage.datastore.DataStoreProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import se.ansman.dagger.auto.AutoBind
import java.io.File
import javax.inject.Inject

@AutoBind
class DataStoreProviderImpl @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
    @ApplicationContext private val applicationContext: Context
) : DataStoreProvider {
    override fun <T> create(
        serializer: Serializer<T>,
        corruptionHandler: ReplaceFileCorruptionHandler<T>?,
        migrations: List<DataMigration<T>>,
        fileName: String,
    ): DataStore<T> = DataStoreFactory.create(
        serializer = serializer,
        corruptionHandler = corruptionHandler,
        produceFile = {
            applicationContext.dataStoreFile(fileName)
        },
        scope = applicationScope.childSupervisorScope(dispatcherProvider.io),
        migrations = migrations
    )
}
