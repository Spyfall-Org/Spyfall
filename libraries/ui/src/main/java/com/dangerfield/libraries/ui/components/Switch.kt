package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    androidx.compose.material3.Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = OddOneOutTheme.colors.onSurfacePrimary.color,
            uncheckedTrackColor = OddOneOutTheme.colors.surfacePrimary.color,
            uncheckedBorderColor = OddOneOutTheme.colors.onSurfacePrimary.color,
            checkedThumbColor = OddOneOutTheme.colors.onAccent.color,
            checkedTrackColor = OddOneOutTheme.colors.accent.color,
            checkedBorderColor = OddOneOutTheme.colors.accent.color,
            disabledCheckedBorderColor = OddOneOutTheme.colors.onSurfaceDisabled.color,
            disabledUncheckedBorderColor = OddOneOutTheme.colors.onSurfaceDisabled.color,
            disabledCheckedThumbColor = OddOneOutTheme.colors.onSurfaceDisabled.color,
            disabledUncheckedThumbColor = OddOneOutTheme.colors.onSurfaceDisabled.color,
            disabledCheckedTrackColor = OddOneOutTheme.colors.surfaceDisabled.color,
            disabledUncheckedTrackColor = OddOneOutTheme.colors.surfaceDisabled.color
        )
    )
}

@Composable
@Preview
private fun Unchecked() {
    Preview {
        Switch(checked = false, onCheckedChange = {})
    }
}

@Composable
@Preview
private fun Checked() {
    Preview {
        Switch(checked = true, onCheckedChange = {})
    }
}

@Composable
@Preview
private fun CheckedDisabled() {
    Preview {
        Switch(
            checked = true,
            onCheckedChange = {},
            enabled = false
        )
    }
}

@Composable
@Preview
private fun UncheckedDisabled() {
    Preview {
        Switch(
            checked = false,
            onCheckedChange = {},
            enabled = false
        )
    }
}

