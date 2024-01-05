package com.dangerfield.libraries.ui.components.radio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Sizes
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.StandardBorderWidth
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

/**
 * A radio button represents a single choice of many.
 *
 * A radio button must be placed inside a [RadioGroup], either as a direct or as an indirect child.
 */
@Composable
fun RadioButton(
    state: RadioButtonState,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    LocalRadioGroupState.current.Register(state)
    RadioButton(
        selected = state.selected,
        onClick = { state.onClicked() },
        modifier = modifier,
        key = state.animationKey,
        interactionSource = interactionSource
    )
}

@Composable
private fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    key: Int = 0,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val selectedProgress by key(key) {
        animateFloatAsState(
            targetValue = if (selected) 1f else 0f,
            label = "selected"
        )
    }
    val radioColor by key(key) {
        animateColorAsState(
            targetValue = if (selected) {
                OddOneOutTheme.colorScheme.text
            } else {
                OddOneOutTheme.colorScheme.border
            }.color,
            label = "Color"
        )
    }
    Canvas(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = true,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = RadioButton.RippleRadius
                )
            )
            .wrapContentSize(Alignment.Center)
            .padding(RadioButton.Padding)
            .size(RadioButton.Size)
    ) {
        val strokeWidth = lerp(
            RadioButton.BorderWidth.toPx(),
            (size.width - RadioButton.DotSize.toPx()) / 2,
            selectedProgress
        )
        drawCircle(
            radioColor,
            radius = size.width / 2 - strokeWidth / 2,
            style = Stroke(strokeWidth)
        )
    }
}

object RadioButton {
    val BorderWidth = StandardBorderWidth
    val Size = Sizes.S800
    val Padding = Spacing.S300
    val DotSize = Sizes.S200
    val RippleRadius = Sizes.S1200 / 2
}

@Preview
@Composable
private fun RadioButtonPreview() {
    PreviewContent {
        RadioGroup {
            Column {
                RadioButton(rememberRadioButtonState(initialState = true))
                RadioButton(rememberRadioButtonState(initialState = false))
            }
        }
    }
}
