package com.dangerfield.spyfall.welcome

import spyfallx.coregameapi.Session

interface WelcomeNavigator {
    fun navigateToSettings()
    fun navigateToNewGame()
    fun navigateToJoinGame()
    fun navigateToWelcome(session: Session?)
    fun navigateToRules()
    fun navigateToSplash()
}
