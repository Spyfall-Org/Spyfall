package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
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
            uncheckedThumbColor = OddOneOutTheme.colorScheme.onSurfacePrimary.color,
            uncheckedTrackColor = OddOneOutTheme.colorScheme.surfacePrimary.color,
            uncheckedBorderColor = OddOneOutTheme.colorScheme.onSurfacePrimary.color,
            checkedThumbColor = OddOneOutTheme.colorScheme.onAccent.color,
            checkedTrackColor = OddOneOutTheme.colorScheme.accent.color,
            checkedBorderColor = OddOneOutTheme.colorScheme.accent.color,
            disabledCheckedBorderColor = OddOneOutTheme.colorScheme.surfaceDisabled.color,
            disabledUncheckedBorderColor = OddOneOutTheme.colorScheme.surfaceDisabled.color,
        )
    )
}

@Composable
@ThemePreviews
private fun Unchecked() {
    PreviewContent {
        Switch(checked = false, onCheckedChange = {})
    }
}

@Composable
@ThemePreviews
private fun Checked() {
    PreviewContent {
        Switch(checked = true, onCheckedChange = {})
    }
}
