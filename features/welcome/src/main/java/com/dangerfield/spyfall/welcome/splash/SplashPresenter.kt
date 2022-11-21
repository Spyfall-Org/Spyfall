package com.dangerfield.spyfall.welcome.splash

import com.dangerfield.spyfall.welcome.WelcomeNavigator
import com.dangerfield.spyfall.welcome.splash.SplashViewModel.GameStatus
import spyfallx.core.doNothing
import javax.inject.Inject

class SplashPresenter @Inject constructor(private val navigator: WelcomeNavigator) {

    fun bindState(state: SplashViewModel.State) {
        when (state.gameStatus) {
            is GameStatus.FoundInGame -> navigator.navigateToWelcome(state.gameStatus.session)
            GameStatus.NotFoundInGame -> navigator.navigateToWelcome(null)
            GameStatus.SearchingForGame -> doNothing()
        }
    }
}

