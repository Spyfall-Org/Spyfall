package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import spyfallx.ui.theme.SpyfallTheme

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
            uncheckedThumbColor = SpyfallTheme.colorScheme.onSurfacePrimary.color,
            uncheckedTrackColor = SpyfallTheme.colorScheme.surfacePrimary.color,
            uncheckedBorderColor = SpyfallTheme.colorScheme.onSurfacePrimary.color,
            checkedThumbColor = SpyfallTheme.colorScheme.onAccent.color,
            checkedTrackColor = SpyfallTheme.colorScheme.accent.color,
            checkedBorderColor = SpyfallTheme.colorScheme.accent.color,
            disabledCheckedBorderColor = SpyfallTheme.colorScheme.surfaceDisabled.color,
            disabledUncheckedBorderColor = SpyfallTheme.colorScheme.surfaceDisabled.color,
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
