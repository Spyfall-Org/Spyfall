package com.dangerfield.spyfall.welcome.splash

import com.dangerfield.spyfall.welcome.splash.SplashViewModel.GameStatus.*
import com.dangerfield.spyfall.welcome.welcome.WelcomeNavigator
import spyfallx.core.doNothing

class SplashPresenter(
    private val navigator: WelcomeNavigator
) {

    fun bindState(state: SplashViewModel.State) {
        when(state.gameStatus) {
            is FoundInGame -> doNothing()
            NotFoundInGame -> navigator.navigateToWelcome(null)
            SearchingForGame -> doNothing()
        }
    }
}

typealias SplashPresenterFactory = (SplashFragment) -> SplashPresenter
