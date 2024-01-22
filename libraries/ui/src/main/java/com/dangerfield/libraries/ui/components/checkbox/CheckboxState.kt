package com.dangerfield.libraries.ui.components.checkbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue

@Composable
fun rememberCheckboxState(initialState: Boolean, onConfirm: (Boolean) -> Boolean = { true }) =
    remember { CheckboxState(initialState, onConfirm) }
        .also { it.onConfirm = onConfirm }

@Stable
class CheckboxState(
    initialState: Boolean,
    internal var onConfirm: (Boolean) -> Boolean = { true }
) {
    // We keep this outside of a state to avoid reading the state when setting the selected state
    // because we don't accidentally want to trigger infinite recompositions.
    private var currentKey = 0
    private var _checked by mutableStateOf(initialState)

    // This key is used to key animations in the checkbox. If you change this key while setting the checked state
    // it will skip animating.
    internal var animationKey by mutableIntStateOf(currentKey)
        private set

    var checked: Boolean
        get() = _checked
        set(value) {
            _checked = value
            animationKey = ++currentKey
        }

    fun uncheckAnimated() {
        setCheckedAnimated(false)
    }

    fun checkAnimated() {
        setCheckedAnimated(true)
    }

    fun setCheckedAnimated(checked: Boolean) {
        _checked = checked
    }

    fun onClicked(checked: Boolean = !this.checked) {
        if (onConfirm(checked)) {
            setCheckedAnimated(checked)
        }
    }

    companion object {
        @Suppress("FunctionNaming")
        fun Saver(onConfirm: (Boolean) -> Boolean): Saver<CheckboxState, *> = Saver(
            save = { it.checked },
            restore = { CheckboxState(it, onConfirm) }
        )
    }
}
