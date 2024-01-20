package com.dangerfield.features.videoCall.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.iconTopAccessory
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.oddoneoout.features.videocall.internal.R

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
        topAccessory = iconTopAccessory(icon = SpyfallIcon.VideoCall(null)),
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.videoLink_infoDialog_title))
        },
        content = {
            ScrollingColumnWithFadingEdge {
                Text(
                    text = dictionaryString(R.string.videoLink_infoDialogBody_text),
                )
                if (recognizedPlatforms.isNotEmpty()) {
                    VerticalSpacerS800()
                    Text(dictionaryString(R.string.videoLink_infoDialogSaftey_header))
                    VerticalSpacerS500()
                    NonLazyVerticalGrid(
                        columns = 2,
                        data = recognizedPlatforms
                    ) { _, item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = dictionaryString(R.string.bullet_point))
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
                Text(text = dictionaryString(R.string.app_okay_action))
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
