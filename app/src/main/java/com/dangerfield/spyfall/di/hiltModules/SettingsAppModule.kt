package com.dangerfield.spyfall.di.hiltModules

import com.dangerfield.spyfall.settings.SettingsFragment
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck

@Module(includes = [SettingsAppModule.Bindings::class])
@InstallIn(SingletonComponent::class)
object SettingsAppModule {

    @Module
    @DisableInstallInCheck
    interface Bindings {

    }

    @Provides
    fun provideSettingsFragmentFactory() : SettingsFragmentFactory = SettingsFragment
}