package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.Dialog
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.text.Text
import spyfallx.ui.Spacing

@Composable
fun VideoCallLinkInfoDialog(
    modifier: Modifier = Modifier,
    recognizedPlatforms: List<String>,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismiss = onDismiss,
        modifier = modifier,
        topContent = {
            Text(text = "Add video calling to your game")
        },
        content = {
            Column {
                Text(
                    text = "You can add a video link when creating your game to make it easier to play with anyone anywhere. When a player joins the game they will also have access to this link.",
                )
                if (recognizedPlatforms.isNotEmpty()) {
                    VerticalSpacerS800()
                    Text("To ensure user safety we only accept links from the following platforms:")
                    VerticalSpacerS500()
                    NonLazyVerticalGrid(
                        columns = 2,
                        data = recognizedPlatforms
                    ) { index, item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "•")
                            HorizontalSpacerS600()
                            Text(text = item)
                        }
                    }

                    VerticalSpacerS800()

                }
            }
        },
        bottomContent = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.S800),
                onClick = onDismiss
            ) {
                Text(text = "Okay")
            }
        }
    )
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
