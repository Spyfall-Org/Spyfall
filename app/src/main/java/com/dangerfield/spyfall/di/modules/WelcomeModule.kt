package com.dangerfield.spyfall.di.modules

import com.dangerfield.spyfall.welcome.welcome.WelcomeFragment
import com.dangerfield.spyfall.welcome.welcome.WelcomeNavigator
import com.dangerfield.spyfall.welcome.welcome.WelcomeNavigatorImpl
import com.dangerfield.spyfall.welcome.welcome.WelcomePresenterFactory
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.dsl.module

val welcomeModule = module {

    factory { WelcomePresenterFactory(get()) }
    factory<WelcomeFragmentFactory> { WelcomeFragment.Companion }
    factory<WelcomeNavigator> { WelcomeNavigatorImpl(get()) }

}