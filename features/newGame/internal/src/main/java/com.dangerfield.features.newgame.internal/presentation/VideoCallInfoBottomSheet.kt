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
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.modal.ModalContent
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.Text
import kotlinx.coroutines.launch
import spyfallx.ui.Spacing

@Composable
fun VideoCallInfoBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    recognizedPlatforms: List<String>,
    onDismiss: (BottomSheetState) -> Unit
) {
    BasicBottomSheet(
        onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
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
                    ) { _, item ->
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
                onClick = { onDismiss(bottomSheetState) }
            ) {
                Text(text = "Okay")
            }
        }
    )
}

@Composable
@ThemePreviews
private fun PreviewVideoCallLinkInfoDialog() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    PreviewContent {
        VideoCallInfoBottomSheet(
            onDismiss = {},
            bottomSheetState = bottomSheetState,
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
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    PreviewContent {
        VideoCallInfoBottomSheet(
            onDismiss = {},
            bottomSheetState = bottomSheetState,
            recognizedPlatforms = emptyList()
        )
    }
}
