package com.dangerfield.libraries.ui.components.radio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun rememberRadioButtonState(initialState: Boolean, onConfirm: () -> Boolean = { true }) =
    rememberSaveable(saver = RadioButtonState.Saver(onConfirm)) { RadioButtonState(initialState, onConfirm) }
        .also { it.onConfirm = onConfirm }

@Stable
class RadioButtonState(initialState: Boolean, internal var onConfirm: () -> Boolean = { true }) {
    // We keep this outside of a state to avoid reading the state when setting the selected state
    // because we don't accidentally want to trigger infinite recompositions.
    private var _animationKey = 0
    private var _selected by mutableStateOf(initialState)

    // This key is used to key animations in the radio button. If you change this key while setting the selected state
    // it will skip animating.
    internal var animationKey by mutableIntStateOf(_animationKey)
        private set

    var selected: Boolean
        get() = _selected
        set(value) {
            _selected = value
            animationKey = ++_animationKey
        }

    fun deselectAnimated() {
        setSelectedAnimated(false)
    }

    fun selectAnimated() {
        setSelectedAnimated(true)
    }

    fun setSelectedAnimated(selected: Boolean) {
        this._selected = selected
    }

    fun onClicked() {
        if (!selected && onConfirm()) {
            selectAnimated()
        }
    }

    companion object {
        @Suppress("FunctionNaming")
        fun Saver(onConfirm: () -> Boolean): Saver<RadioButtonState, *> = Saver(
            save = { it.selected },
            restore = { RadioButtonState(it, onConfirm) }
        )
    }
}
