package com.dangerfield.spyfall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.spyfall.MainActivityViewModel.Step.ForceUpdateDecision
import com.dangerfield.spyfall.MainActivityViewModel.Step.SplashDecision
import com.dangerfield.spyfall.splash.CheckForRequiredUpdate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import spyfallx.core.BuildInfo

class MainActivityViewModel(
    private val checkForForcedUpdate: CheckForRequiredUpdate,
    private val buildInfo: BuildInfo
) : ViewModel() {

    val state: StateFlow<State> = flow {
        val shouldRequireUpdate = buildInfo.isLegacySpyfall && checkForForcedUpdate.shouldRequireUpdate()
        emit(
            State(step = ForceUpdateDecision(shouldRequireUpdate))
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = State(
                step = SplashDecision(shouldShowSplash = !buildInfo.isLegacySpyfall)
            )
        )

    sealed class Step {
        class SplashDecision(val shouldShowSplash: Boolean) : Step()
        class ForceUpdateDecision(val shouldShowForceUpdate: Boolean) : Step()
    }

    data class State(
        val step: Step,
    )
}
