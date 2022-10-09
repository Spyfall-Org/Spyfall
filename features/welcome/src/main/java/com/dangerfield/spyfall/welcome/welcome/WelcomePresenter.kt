package com.dangerfield.spyfall.welcome.welcome

import com.dangerfield.spyfall.welcome.databinding.FragmentWelcomeBinding
import spyfallx.coreui.supportFragmentManager

class WelcomePresenter(
    private val fragment: WelcomeFragment,
    private val navigator: WelcomeNavigator
) {

    private val binding = FragmentWelcomeBinding.bind(fragment.requireView())

    init {
        setupView()
    }

    private fun setupView() {
        binding.btnJoinGame.setOnClickListener {
            navigator.navigateToJoinGame(fragment.supportFragmentManager)
        }

        binding.btnNewGame.setOnClickListener {
            navigator.navigateToNewGame(fragment.supportFragmentManager)
        }

        binding.btnSettings.setOnClickListener {
            navigator.navigateToSettings(fragment.supportFragmentManager)
        }

        binding.btnRules.setOnClickListener {
            navigator.navigateToRules(fragment.supportFragmentManager)
        }
    }
}

class WelcomePresenterFactory(
    private val welcomeNavigator: WelcomeNavigator
) {
    fun create(fragment: WelcomeFragment) = WelcomePresenter(fragment, welcomeNavigator)
}