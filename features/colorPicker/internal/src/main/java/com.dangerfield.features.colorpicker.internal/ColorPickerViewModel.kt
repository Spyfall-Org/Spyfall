package com.dangerfield.features.colorpicker.internal

import com.dangerfield.features.colorpicker.internal.ColorPickerViewModel.*
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateColorConfig
import com.dangerfield.libraries.session.UpdateDarkModeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColorPickerViewModel @Inject constructor(
    private val updateColorConfig: UpdateColorConfig,
    private val updateDarkModeConfig: UpdateDarkModeConfig
) : SEAViewModel<State, Unit, Action>() {

    override val initialState = State(
        colorConfig = ColorConfig.Random,
        darkModeConfig = DarkModeConfig.System
    )

    override suspend fun handleAction(action: Action) {
        when(action) {
            is Action.UpdateColorConfig -> {
                updateState {
                    it.copy(colorConfig = action.config)
                }
                updateColorConfig(action.config)
            }
            is Action.UpdateDarkModeConfig -> {
                updateState {
                    it.copy(darkModeConfig = action.config)
                }
                updateDarkModeConfig(action.config)
            }
        }
    }

    data class State(
        val colorConfig: ColorConfig,
        val darkModeConfig: DarkModeConfig
    )

    sealed class Action {
        data class UpdateColorConfig(val config: ColorConfig) : Action()
        data class UpdateDarkModeConfig(val config: DarkModeConfig) : Action()
    }
}