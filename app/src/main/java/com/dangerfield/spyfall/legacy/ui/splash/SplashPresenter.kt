package com.dangerfield.spyfall.legacy.ui.splash

import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.FragmentSplashBinding
import com.dangerfield.spyfall.legacy.ui.splash.SplashViewModel.GameStatus
import dagger.hilt.android.scopes.FragmentScoped
import spyfallx.core.doNothing
import javax.inject.Inject

@FragmentScoped
class SplashPresenter @Inject constructor(
    fragment: Fragment,
    private val navigator: SplashNavigator,
) {

    private val binding = FragmentSplashBinding.bind(fragment.requireView())

    init {

        val drawable = AppCompatResources.getDrawable(binding.root.context, R.drawable.spyfall_logo_transparent)

        binding.logo.setImageDrawable(drawable)
    }

    fun bindState(state: SplashViewModel.State) {
        if (state.isUpdateRequired) {
            Log.d("Elijah", "GOT UPDATE IS REQUIRED")
            navigator.navigateToForcedUpdate()
        } else {
            when (state.gameStatus) {
                is GameStatus.FoundInGame -> navigator.navigateToWelcome(state.gameStatus.session)
                GameStatus.NotFoundInGame -> navigator.navigateToWelcome(null)
                GameStatus.SearchingForGame -> doNothing()
            }
        }
    }
}
