package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ProvideButtonConfig
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.modifiers.drawVerticalScrollbar
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.ui.Radii
import spyfallx.ui.Spacing
import spyfallx.ui.clip
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid

@Composable
fun VideoCallLinkInfoDialog(
    modifier: Modifier = Modifier,
    recognizedPlatforms: List<String>,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = modifier
                .clip(Radii.Card)
                .verticalScroll(scrollState)
                .drawVerticalScrollbar(scrollState, SpyfallTheme.colorScheme.surfaceDisabled.color)
                .background(SpyfallTheme.colorScheme.surfacePrimary)
                .padding(horizontal = Spacing.S800),
            horizontalAlignment = Alignment.Start,
        ) {

            VerticalSpacerS500()

            Text(text = "Add video calling to your game")

            VerticalSpacerS800()

            ProvideTextConfig(SpyfallTheme.typography.Body.B500) {
                Text(
                    text = "You can add a video link when creating your game to make it easier to play with anyone anywhere. When a player joins the game they will also have access to this link.",
                )
                if (recognizedPlatforms.isNotEmpty()) {
                    VerticalSpacerS800()
                    Text("To ensure user safety we only accept links from the following platforms:")
                    VerticalSpacerS500()
                    NonLazyVerticalGrid(columns = 2, data = recognizedPlatforms) { index, item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "•")
                            HorizontalSpacerS600()
                            Text(text = item)
                        }
                    }

                    VerticalSpacerS800()

                }
            }


            ProvideButtonConfig(size = ButtonSize.Small) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.S800),
                    onClick = onDismiss
                ) {
                    Text(text = "Okay")
                }
            }

            VerticalSpacerS1200()
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewVideoCallLinkInfoDialog() {
    PreviewContent {
        VideoCallLinkInfoDialog(
            onDismiss = {},
            recognizedPlatforms = listOf(
                "Zoom",
                "Google Meet",
                "Skype",
                "Discord",
                "FaceTime",
                "WebEx"
            )
        )
    }
}

@Composable
@ThemePreviews
private fun PreviewVideoCallLinkInfoDialogEmpty() {
    PreviewContent {
        VideoCallLinkInfoDialog(
            onDismiss = {},
            recognizedPlatforms = emptyList()
        )
    }
}
