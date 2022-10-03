package com.dangerfield.spyfall.di.modules

import com.dangerfield.spyfall.settings.SettingsFragment
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import org.koin.dsl.module

val settingsModule = module {

    factory { SettingsFragment.Companion as SettingsFragmentFactory }
}