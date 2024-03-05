package com.dangerfield.libraries.ui.components.radio

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun NewRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {

    androidx.compose.material3.RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors.toMaterial(),
        interactionSource = interactionSource
    )
}

@Preview
@Composable
private fun PreviewButton() {
    Preview {
        var selected by remember { mutableStateOf(false) }
        NewRadioButton(selected = selected, onClick = { selected = !selected})
    }
}

@Preview
@Composable
private fun PreviewButtonUnselected() {
    Preview {
        NewRadioButton(selected = false, onClick = { })
    }
}

@Preview
@Composable
private fun PreviewButtonSelected() {
    Preview {
        NewRadioButton(selected = true, onClick = { })
    }
}

@Preview
@Composable
private fun PreviewButtonUnselectedDisabled() {
    Preview {
        NewRadioButton(selected = false, onClick = { }, enabled = false)
    }
}

@Preview
@Composable
private fun PreviewButtonSelectedDisabled() {
    Preview {
        NewRadioButton(selected = true, onClick = { }, enabled = false)
    }
}
object RadioButtonDefaults {
    @Composable
    fun colors(
        selectedColor: Color = OddOneOutTheme.colors.onBackground.color,
        unselectedColor: Color = OddOneOutTheme.colors.onBackground.color,
        disabledSelectedColor: Color = OddOneOutTheme.colors.textDisabled.color,
        disabledUnselectedColor: Color = OddOneOutTheme.colors.textDisabled.color
    ): RadioButtonColors = RadioButtonColors(
        selectedColor,
        unselectedColor,
        disabledSelectedColor,
        disabledUnselectedColor
    )
}

@Immutable
data class RadioButtonColors (
    val selectedColor: Color,
    val unselectedColor: Color,
    val disabledSelectedColor: Color,
    val disabledUnselectedColor: Color
)

private fun RadioButtonColors.toMaterial() = androidx.compose.material3.RadioButtonColors(
    selectedColor = selectedColor,
    unselectedColor = unselectedColor,
    disabledSelectedColor = disabledSelectedColor,
    disabledUnselectedColor = disabledUnselectedColor
)