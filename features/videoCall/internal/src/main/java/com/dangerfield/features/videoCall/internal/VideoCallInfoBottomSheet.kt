package com.dangerfield.features.videoCall.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.HorizontalSpacerD600
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
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
        modifier = modifier,
        stickyTopContent = {
            Text(text = dictionaryString(R.string.videoLink_infoDialog_title))
        },
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = dictionaryString(R.string.videoLink_infoDialogBody_text),
                )
                if (recognizedPlatforms.isNotEmpty()) {
                    VerticalSpacerD800()
                    Text(dictionaryString(R.string.videoLink_infoDialogSafety_header))
                    VerticalSpacerD500()
                    NonLazyVerticalGrid(
                        columns = 2,
                        data = recognizedPlatforms
                    ) { _, item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = dictionaryString(R.string.app_bullet_point))
                            HorizontalSpacerD600()
                            Text(text = item)
                        }
                    }

                    VerticalSpacerD800()

                }
            }
        },
        stickyBottomContent = {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onDismiss(bottomSheetState) }
            ) {
                Text(text = dictionaryString(R.string.app_okay_action))
            }
        }
    )
}

@Composable
@Preview
private fun PreviewVideoCallLinkInfoDialog() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    Preview {
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
@Preview
private fun PreviewVideoCallLinkInfoDialogEmpty() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    Preview {
        VideoCallInfoBottomSheet(
            onDismiss = {},
            bottomSheetState = bottomSheetState,
            recognizedPlatforms = emptyList()
        )
    }
}
