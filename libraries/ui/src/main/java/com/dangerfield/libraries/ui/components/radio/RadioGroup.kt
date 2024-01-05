package com.dangerfield.libraries.ui.components.radio

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

/**
 * A radio group is a collection of [RadioButton]. It ensures that only a single radio button can be selected at any
 * given time.
 *
 * The radio buttons does not have to be direct children of the group.
 */
@Composable
fun RadioGroup(
    state: RadioGroupState = rememberRadioGroupState(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalRadioGroupState provides state, content = content)
}

@Composable
fun rememberRadioGroupState(): RadioGroupState = remember { RadioGroupState() }

@Stable
class RadioGroupState internal constructor() {
    private val registered = mutableSetOf<RadioButtonState>()
    private var selected: RadioButtonState? = null

    fun deselectAll() {
        registered.forEach { it.selected = false }
    }

    fun deselectAllAnimated() {
        registered.forEach { it.deselectAnimated() }
    }

    @Composable
    internal fun Register(state: RadioButtonState) {
        LaunchedEffect(state) {
            if (!registered.add(state)) {
                error("This radio state is already being used with another radio button.")
            }
            if (state.selected) {
                if (selected == null) {
                    selected = state
                } else {
                    state.selected = false
                }
            }

            try {
                snapshotFlow { state.selected }
                    .collect {
                        if (it && selected != state) {
                            selected?.deselectAnimated()
                            selected = state
                        } else if (!it && selected == state) {
                            selected?.deselectAnimated()
                            selected = null
                        }
                    }
            } finally {
                registered -= state
                if (selected == state) {
                    selected = null
                }
            }
        }
    }
}

internal val LocalRadioGroupState = staticCompositionLocalOf<RadioGroupState> {
    error("RadioButtons must be placed in a RadioGroup")
}

@Preview
@Composable
private fun RadioGroupPreview() {
    PreviewContent {
        val radioGroupState = rememberRadioGroupState()
        RadioGroup(radioGroupState) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                repeat(5) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val state = rememberRadioButtonState(false)
                    Row(
                        modifier = Modifier
                            .clickable(interactionSource, rememberRipple()) { state.onClicked() }
                            .fillMaxWidth()
                            .padding(Spacing.S700)
                    ) {
                        Text(
                            "Option ${it + 1}",
                            modifier = Modifier.weight(1f),
                            typographyToken = OddOneOutTheme.typography.Body.B400
                        )
                        RadioButton(state = state)
                    }
                }
                Button(
                    onClick = { radioGroupState.deselectAllAnimated() },
                ) {
                    Text("Reset Animated")
                }

                Button(
                    onClick = { radioGroupState.deselectAll() },

                    ) {
                    Text("Reset")
                }
            }
        }
    }
}
