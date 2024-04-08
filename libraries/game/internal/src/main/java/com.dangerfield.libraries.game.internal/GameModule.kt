package com.dangerfield.libraries.game.internal

import androidx.datastore.core.DataStore
import com.dangerfield.libraries.game.internal.packs.CachedLocationPack
import com.dangerfield.libraries.storage.datastore.DataStoreJsonSerializer
import com.dangerfield.libraries.storage.datastore.DataStoreProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GameModule {

    @Provides
    @Singleton
    fun provideCachedLocationPacks(
        dataStoreProvider: DataStoreProvider,
        moshi: Moshi
        ): DataStore<List<CachedLocationPack>> {
        return dataStoreProvider.create(
            serializer = DataStoreJsonSerializer(
                defaultValue = emptyList(),
                provideJsonAdapter = {
                    val type = Types.newParameterizedType(List::class.java,CachedLocationPack::class.java)
                    moshi.adapter(type)
                },
            ),
            corruptionHandler = null,
            fileName = "location_packs"
        )
    }
}
