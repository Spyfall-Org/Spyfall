package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.PreviewContent
import androidx.compose.ui.tooling.preview.Preview
@Composable
fun SettingsOption(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: SpyfallIcon? = null,
    trailingIcon: SpyfallIcon? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick { onClick() }
            .padding(vertical = Spacing.S500,),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let {
            Icon(spyfallIcon = it, iconSize = IconSize.Large)
            Spacer(modifier = Modifier.width(Spacing.S500))
        }
        text()
        Spacer(modifier = Modifier.weight(1f))
        trailingIcon?.let { Icon(spyfallIcon = it, iconSize = IconSize.Large) }
    }
}

@Composable
fun SettingsOption(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: SpyfallIcon? = null,
    trailingIcon: SpyfallIcon? = null,
) {
    SettingsOption(
        text = { Text(text = text) },
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}


@Composable
@Preview
fun PreviewSettingsOption() {
    PreviewContent(showBackground = true) {
        SettingsOption(
            leadingIcon = SpyfallIcon.Settings("Example"),
            text = "Example",
            onClick = {}
        )
    }
}

@Composable
@Preview
fun PreviewSettingsOptionTrailing() {
    PreviewContent(showBackground = true) {
        SettingsOption(
            trailingIcon = SpyfallIcon.ChevronRight("Example"),
            text = "Example",
            onClick = {}
        )
    }
}