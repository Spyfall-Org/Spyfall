package com.dangerfield.features.forcedupdate.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
@Suppress("MagicNumber")
fun ForcedUpdateScreen(
    onOpenAppStoreClicked: () -> Unit
) {
    Screen { paddingValues ->
        Column(
            Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.10f))
            Text(
                text = "We've made some exciting changes",
                typographyToken = OddOneOutTheme.typography.Display.D1000,
                modifier = Modifier.padding(horizontal = Spacing.S500),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.S500))

            Text(
                text = "Please download the latest app from the app store to enjoy them.",
                typographyToken = OddOneOutTheme.typography.Body.B700,
                modifier = Modifier.padding(horizontal = Spacing.S500),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = Spacing.S1100),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onOpenAppStoreClicked,
                    modifier = Modifier.fillMaxWidth(),
                    type = ButtonType.Accent
                ) {
                    Text(text = "Open App Store")
                }

                Spacer(modifier = Modifier.height(Spacing.S1000))

            }
            Spacer(modifier = Modifier.height(Spacing.S1000))
        }
    }
}

@Composable
@Preview
private fun PreviewForcedUpdateScreen() {
    PreviewContent {
        ForcedUpdateScreen(
            onOpenAppStoreClicked = {},
        )
    }
}

