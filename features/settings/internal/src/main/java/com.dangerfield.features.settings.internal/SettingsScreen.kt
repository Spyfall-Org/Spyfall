package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Spacing
import spyfallx.coreui.components.Screen
import spyfallx.coreui.components.header.Header
import spyfallx.coreui.components.text.Text

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Screen(
        modifier = modifier,
        header = {
            Header(title = "Settings")
        }
    ) {
        Column(
            modifier = Modifier.padding(it).padding(horizontal = Spacing.S1000)
        ) {
            Text(text = "Option 1")
            Text(text = "Option 2")
            Text(text = "Option 3")
            Text(text = "Option 4")

        }
    }
}



@Composable
@Preview
private fun PreviewSettingsScreen() {
    PreviewContent {
        SettingsScreen()
    }
}
