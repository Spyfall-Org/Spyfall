package com.dangerfield.libraries.ui.components.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    androidx.compose.material3.Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors.toMaterial(),
        interactionSource = interactionSource

    )
}

@Composable
@Preview
private fun Unchecked() {
    Preview {
        Checkbox(checked = false, onCheckedChange = {})
    }
}

@Composable
@Preview
private fun Checked() {
    Preview {
        Checkbox(checked = true, onCheckedChange = {})
    }
}

@Composable
@Preview
private fun DisabledChecked() {
    Preview {
        Checkbox(
            checked = true,
            enabled = false,
            onCheckedChange = {}
        )
    }
}

@Composable
@Preview
private fun DisabledUnChecked() {
    Preview {
        Checkbox(
            checked = false,
            enabled = false,
            onCheckedChange = {}
        )
    }
}


object CheckboxDefaults {
    @Composable
    fun colors() = CheckboxColors(
        checkedCheckmarkColor = OddOneOutTheme.colors.background.color,
        uncheckedCheckmarkColor = Color.Transparent,
        checkedBoxColor = OddOneOutTheme.colors.accent.color,
        uncheckedBoxColor = Color.Transparent,
        disabledCheckedBoxColor = OddOneOutTheme.colors.textDisabled.color,
        disabledUncheckedBoxColor = OddOneOutTheme.colors.textDisabled.color,
        disabledIndeterminateBoxColor = OddOneOutTheme.colors.textDisabled.color,
        checkedBorderColor = OddOneOutTheme.colors.accent.color,
        uncheckedBorderColor = OddOneOutTheme.colors.border.color,
        disabledBorderColor = OddOneOutTheme.colors.textDisabled.color,
        disabledUncheckedBorderColor = OddOneOutTheme.colors.textDisabled.color,
        disabledIndeterminateBorderColor = OddOneOutTheme.colors.textDisabled.color
    )
}

@Immutable
data class CheckboxColors (
    val checkedCheckmarkColor: Color,
    val uncheckedCheckmarkColor: Color,
    val checkedBoxColor: Color,
    val uncheckedBoxColor: Color,
    val disabledCheckedBoxColor: Color,
    val disabledUncheckedBoxColor: Color,
    val disabledIndeterminateBoxColor: Color,
    val checkedBorderColor: Color,
    val uncheckedBorderColor: Color,
    val disabledBorderColor: Color,
    val disabledUncheckedBorderColor: Color,
    val disabledIndeterminateBorderColor: Color
)

private fun CheckboxColors.toMaterial() = androidx.compose.material3.CheckboxColors(
    checkedCheckmarkColor = checkedCheckmarkColor,
    uncheckedCheckmarkColor = uncheckedCheckmarkColor,
    checkedBoxColor = checkedBoxColor,
    uncheckedBoxColor = uncheckedBoxColor,
    disabledCheckedBoxColor = disabledCheckedBoxColor,
    disabledUncheckedBoxColor = disabledUncheckedBoxColor,
    disabledIndeterminateBoxColor = disabledIndeterminateBoxColor,
    checkedBorderColor = checkedBorderColor,
    uncheckedBorderColor = uncheckedBorderColor,
    disabledBorderColor = disabledBorderColor,
    disabledUncheckedBorderColor = disabledUncheckedBorderColor,
    disabledIndeterminateBorderColor = disabledIndeterminateBorderColor
)