package com.dangerfield.spyfall.welcome.splash

import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import com.dangerfield.spyfall.welcome.splash.SplashViewModel.GameStatus
import com.dangerfield.spyfall.welcome.welcome.WelcomeNavigator
import com.dangerfield.spyfall.welcome.welcome.WelcomeNavigatorImpl
import spyfallx.core.doNothing
import spyfallx.coreui.supportFragmentManager

class SplashPresenter(
    private val navigator: WelcomeNavigator
) {

    fun bindState(state: SplashViewModel.State) {
        when(state.gameStatus) {
            is GameStatus.FoundInGame -> navigator.navigateToWelcome(state.gameStatus.session)
            GameStatus.NotFoundInGame -> navigator.navigateToWelcome(null)
            GameStatus.SearchingForGame -> doNothing()
        }
    }
}

class SplashPresenterFactory(private val settingsFragmentFactory: SettingsFragmentFactory) {
    fun create(fragment: SplashFragment) = SplashPresenter(WelcomeNavigatorImpl(settingsFragmentFactory, fragment.supportFragmentManager))
}