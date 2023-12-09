package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.AppConfigMap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow

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
}
