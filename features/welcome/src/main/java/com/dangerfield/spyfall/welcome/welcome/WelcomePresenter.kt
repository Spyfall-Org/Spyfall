package com.dangerfield.spyfall.welcome.welcome

import com.dangerfield.spyfall.welcome.databinding.FragmentWelcomeBinding

class WelcomePresenter(
    fragment: WelcomeFragment,
    private val navigator: WelcomeNavigator
) {

    private val binding = FragmentWelcomeBinding.bind(fragment.requireView())

    init {
        setupView()
    }

    private fun setupView() {
        binding.btnJoinGame.setOnClickListener {
            navigator.navigateToJoinGame()
        }

        binding.btnNewGame.setOnClickListener {
            navigator.navigateToNewGame()
        }

        binding.btnSettings.setOnClickListener {
            navigator.navigateToSettings()
        }

        binding.btnRules.setOnClickListener {
            navigator.navigateToRules()
        }
    }
}
