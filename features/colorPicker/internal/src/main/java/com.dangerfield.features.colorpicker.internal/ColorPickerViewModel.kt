package com.dangerfield.features.colorpicker.internal

import com.dangerfield.features.colorpicker.internal.ColorPickerViewModel.Action
import com.dangerfield.features.colorpicker.internal.ColorPickerViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateColorConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColorPickerViewModel @Inject constructor(
    private val updateColorConfig: UpdateColorConfig,
    private val session: Session
) : SEAViewModel<State, Unit, Action>() {

    override val initialState = State(
        colorConfig = session.user.themeConfig.colorConfig,
    )

    override suspend fun handleAction(action: Action) {
        when(action) {
            is Action.UpdateColorConfig -> {
                updateState {
                    it.copy(colorConfig = action.config)
                }
                updateColorConfig(action.config)
            }
        }
    }

    data class State(
        val colorConfig: ColorConfig,
    )

    sealed class Action {
        data class UpdateColorConfig(val config: ColorConfig) : Action()
    }
}