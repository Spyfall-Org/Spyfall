package com.dangerfield.spyfall.di.hiltModules

import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import com.dangerfield.spyfall.welcome.splash.SplashPresenterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object SplashFragmentModule {

    @Provides
    fun providePresenterFactory(settingsFragmentFactory: SettingsFragmentFactory): SplashPresenterFactory =
        SplashPresenterFactory(settingsFragmentFactory)
}
