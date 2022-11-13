package com.dangerfield.spyfall.di.koinModules

import com.dangerfield.spyfall.welcome.splash.SplashViewModel
import com.dangerfield.spyfall.welcome.welcome.WelcomeFragment
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import spyfallx.core.GamePrefs

val welcomeModule = module {

    factory<WelcomeFragmentFactory> { WelcomeFragment.Companion }

    factory { GamePrefs(get()) }

    viewModel { SplashViewModel(get(), get()) }

}

