package com.dangerfield.features.blockingerror.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Spacing
import spyfallx.coreui.components.Screen
import spyfallx.coreui.components.text.Text
import spyfallx.coreui.theme.SpyfallTheme

@Composable
@Suppress("MagicNumber")
fun BlockingErrorScreen() {
    Screen { paddingValues ->
        Column(
            Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.10f))
            Text(
                text = "Hmmmm...",
                typographyToken = SpyfallTheme.typography.Display.D1000,
                modifier = Modifier.padding(horizontal = Spacing.S500),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Something went wrong. Please restart the app to try again.",
                typographyToken = SpyfallTheme.typography.Body.B700,
                modifier = Modifier.padding(horizontal = Spacing.S500),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }

}


@Composable
@Preview
private fun PreviewBlockingErrorScreen() {
    PreviewContent {
        BlockingErrorScreen()
    }
}
