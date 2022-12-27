package com.dangerfield.spyfall.welcome.welcome

import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.welcome.WelcomeNavigator
import com.dangerfield.spyfall.welcome.databinding.FragmentWelcomeBinding
import dagger.hilt.android.scopes.FragmentScoped
import spyfallx.core.BuildInfo
import javax.inject.Inject

@FragmentScoped
class WelcomePresenter @Inject constructor(
    fragment: Fragment,
    private val navigator: WelcomeNavigator,
    private val buildInfo: BuildInfo
) {

    private val binding = FragmentWelcomeBinding.bind(fragment.requireView())

    init {
        setupView()
    }

    private fun setupView() {

        binding.tvAppName.text = buildInfo.targetApp.appName

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
