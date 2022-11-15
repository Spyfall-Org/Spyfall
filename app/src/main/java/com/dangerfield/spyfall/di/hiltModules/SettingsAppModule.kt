package com.dangerfield.spyfall.di.hiltModules

import com.dangerfield.spyfall.settings.SettingsFragment
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck

@Module
@InstallIn(SingletonComponent::class)
object SettingsAppModule {

    @Provides
    fun provideSettingsFragmentFactory() : SettingsFragmentFactory = SettingsFragment
}
