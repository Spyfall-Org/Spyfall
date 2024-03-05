package com.dangerfield.features.inAppMessaging.internal

import android.content.Context
import androidx.datastore.core.DataStore
import com.dangerfield.features.inAppMessaging.internal.update.InAppUpdateMessage
import com.dangerfield.libraries.storage.datastore.DataStoreProvider
import com.dangerfield.libraries.storage.datastore.VersionedDataSerializer
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InAppMessagingModule {

    private const val SEEN_IN_APP_UPDATE_MESSAGES_FILE = "seenInAppUpdateMessages.json"
    @Provides
    @Singleton
    fun providesAppUpdateManager(@ApplicationContext context: Context): AppUpdateManager {
        return AppUpdateManagerFactory.create(context)
    }

    @Provides
    @Singleton
    fun providesSeenInAppUpdateMessageDatastore(
        dataStoreProvider: DataStoreProvider,
        moshi: Moshi
        ): DataStore<List<InAppUpdateMessage>> {
        return dataStoreProvider.create(
            serializer = VersionedDataSerializer(
                moshi = moshi,
                defaultValue = { emptyList() }
            ),
            corruptionHandler = null,
            fileName = SEEN_IN_APP_UPDATE_MESSAGES_FILE
        )
    }
}
