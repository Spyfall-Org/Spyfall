package com.dangerfield.features.settings.internal

import androidx.datastore.core.DataStore
import com.dangerfield.features.settings.internal.referral.ReferralCode
import com.dangerfield.libraries.storage.datastore.DataStoreProvider
import com.dangerfield.libraries.storage.datastore.OptionalAdapterFactory
import com.dangerfield.libraries.storage.datastore.DatastoreSerializer
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Optional
import javax.inject.Singleton
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    private const val REFERRAL_CODE_FILE = "referralCode.json"

    @OptIn(ExperimentalStdlibApi::class)
    @Provides
    @Singleton
    fun providesSeenInAppUpdateMessageDatastore(
        dataStoreProvider: DataStoreProvider,
        moshi: Moshi,
        ): DataStore<Optional<ReferralCode>> {

        val adapter = OptionalAdapterFactory<ReferralCode>().create(
            typeOf<Optional<ReferralCode>>().javaType,
            annotations = emptySet(),
            moshi
        )

        val serializer = DatastoreSerializer<Optional<ReferralCode>>(
            adapter = adapter,
            migrations = emptyList(),
            defaultValue = {
                Optional.empty<ReferralCode>()
            }
        )

        return dataStoreProvider.create(
            serializer = serializer,
            corruptionHandler = null,
            fileName = REFERRAL_CODE_FILE,
            migrations = emptyList()
        )
    }
}
