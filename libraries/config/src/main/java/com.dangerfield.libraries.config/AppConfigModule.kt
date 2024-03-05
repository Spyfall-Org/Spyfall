package com.dangerfield.libraries.config

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    fun providesAppConfig(appConfigRepository: AppConfigRepository): AppConfigMap {
        return appConfigRepository.config()
    }

    @Provides
    fun providesAppConfigFlow(appConfigRepository: AppConfigRepository): AppConfigFlow {
        return AppConfigFlow(appConfigRepository)
    }
}
