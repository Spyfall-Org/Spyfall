package com.dangerfield.libraries.datastore

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.childSupervisorScope
import com.dangerfield.libraries.flowroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import se.ansman.dagger.auto.AutoBind
import java.io.File

@AutoBind
class DataStoreProviderImpl(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
) : DataStoreProvider {
    override fun <T> create(
        serializer: Serializer<T>,
        corruptionHandler: ReplaceFileCorruptionHandler<T>?,
        migrations: List<DataMigration<T>>,
        produceFile: () -> File,
    ): DataStore<T> = DataStoreFactory.create(
        serializer = serializer,
        corruptionHandler = corruptionHandler,
        produceFile = produceFile,
        scope = applicationScope.childSupervisorScope(dispatcherProvider.io),
        migrations = migrations
    )
}
