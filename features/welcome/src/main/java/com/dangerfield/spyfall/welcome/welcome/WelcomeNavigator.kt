package com.dangerfield.spyfall.welcome.welcome

import spyfallx.core.Session

interface WelcomeNavigator {
    fun navigateToSettings()
    fun navigateToNewGame()
    fun navigateToJoinGame()
    fun navigateToWelcome(session: Session?)
    fun navigateToRules()
}
