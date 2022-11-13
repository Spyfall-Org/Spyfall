package com.dangerfield.spyfall.welcome.splash

import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import com.dangerfield.spyfall.welcome.welcome.WelcomeNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import spyfallx.coreui.supportFragmentManager

@Module
@InstallIn(SingletonComponent::class)
internal object SplashFragmentModule {

    @Provides
    fun providePresenterFactory(settingsFragmentFactory: SettingsFragmentFactory): SplashPresenterFactory =
        SplashPresenterFactory(settingsFragmentFactory)
}
