package com.dangerfield.spyfall.welcome.welcome

import androidx.fragment.app.FragmentManager

interface WelcomeNavigator {
    fun navigateToSettings(fragmentManager: FragmentManager?)
    fun navigateToNewGame(fragmentManager: FragmentManager?)
    fun navigateToJoinGame(fragmentManager: FragmentManager?)
    fun navigateToRules(fragmentManager: FragmentManager?)
}