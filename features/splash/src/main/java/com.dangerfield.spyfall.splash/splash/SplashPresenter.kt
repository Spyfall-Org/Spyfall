package com.dangerfield.spyfall.splash.splash

import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.splash.R
import com.dangerfield.spyfall.splash.splash.SplashViewModel.GameStatus
import com.dangerfield.spyfall.splash.databinding.FragmentSplashBinding
import dagger.hilt.android.scopes.FragmentScoped
import spyfallx.core.BuildInfo
import spyfallx.core.TargetApp
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
