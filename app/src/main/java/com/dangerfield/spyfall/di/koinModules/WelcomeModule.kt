package com.dangerfield.spyfall.di.koinModules

import com.dangerfield.spyfall.welcome.splash.SplashPresenter
import com.dangerfield.spyfall.welcome.splash.SplashPresenterFactory
import com.dangerfield.spyfall.welcome.splash.SplashViewModel
import com.dangerfield.spyfall.welcome.welcome.WelcomeFragment
import com.dangerfield.spyfall.welcome.welcome.WelcomeNavigatorImpl
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import spyfallx.core.GamePrefs
import spyfallx.coreui.supportFragmentManager

val welcomeModule = module {

    factory<WelcomeFragmentFactory> { WelcomeFragment.Companion }

    factory { GamePrefs(get()) }

    viewModel { SplashViewModel(get(), get()) }

    factory<SplashPresenterFactory>(named(SplashPresenter::class.java.name)) {
        {
            SplashPresenter(WelcomeNavigatorImpl(get(), it.supportFragmentManager))
        }
    }
}

