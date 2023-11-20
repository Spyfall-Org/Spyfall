package com.dangerfield.libraries.config.internal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dangerfield.libraries.config.AppConfigMap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    fun providesAppConfigStream(appConfigRepository: OfflineFirstAppConfigRepository): Flow<AppConfigMap> {
        return appConfigRepository.configStream()
    }

    @Provides
    fun providesAppConfig(appConfigRepository: OfflineFirstAppConfigRepository): AppConfigMap {
        return appConfigRepository.config()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return appContext.configDatastore
    }

    private val Context.configDatastore: DataStore<Preferences> by preferencesDataStore(name = "spyfall_config")

}
