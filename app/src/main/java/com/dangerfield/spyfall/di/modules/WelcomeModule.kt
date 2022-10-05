package com.dangerfield.spyfall.di.modules

import com.dangerfield.spyfall.welcome.WelcomeFragment
import com.dangerfield.spyfall.welcome.WelcomeNavigator
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.dsl.module

val welcomeModule = module {

    factory { WelcomeFragment.Companion as WelcomeFragmentFactory }
    factory<WelcomeNavigator> { WelcomeNavigator(get(), get()) }
}