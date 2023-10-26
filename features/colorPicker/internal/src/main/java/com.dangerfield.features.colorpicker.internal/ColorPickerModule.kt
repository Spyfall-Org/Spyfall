package com.dangerfield.features.colorpicker.internal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.dangerfield.features.colorpicker.ColorConfig
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.serializer
import spyfallx.coreui.color.ColorPrimitive
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ColorPickerModule {

    private const val COLOR_PICKER_FILE = "colorpicker.json"

    @Singleton
    @Provides
    fun providesSubscriptionDataStore(
        @ApplicationContext appContext: Context,
        dispatcherProvider: DispatcherProvider
    ): DataStore<ColorConfig> =
        DataStoreFactory.create(
            serializer = VersionedJsonSerializer(
                serializer = serializer(),
                defaultValue = { ColorConfig.Specific(ColorPrimitive.CherryPop700) }
            ),
            scope = CoroutineScope(dispatcherProvider.io), // TODO consider app lifecycle bound scope
            corruptionHandler = ReplaceFileCorruptionHandler { ColorConfig.Specific(ColorPrimitive.CherryPop700) },
            produceFile = { appContext.dataStoreFile(COLOR_PICKER_FILE) }
        )
}
