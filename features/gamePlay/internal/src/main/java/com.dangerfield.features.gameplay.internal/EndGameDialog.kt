package com.dangerfield.features.gameplay.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text

@Composable
fun GamePlayLeaveDialog(
    onDismissRequest: () -> Unit,
    onLeaveConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {

    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(text = "Are you sure?")
        },
        content = {
            Text(text = "Leaving the game will end it for all players.")
        },
        bottomContent = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLeaveConfirmed,
                    type = ButtonType.Regular
                ) {
                    Text(text = "Leave")
                }

                VerticalSpacerS800()

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    )
}

@Composable
@ThemePreviews
private fun WaitingRoomLeavingDialogPreview() {
    PreviewContent {
        GamePlayLeaveDialog(
            onDismissRequest = { -> },
            onLeaveConfirmed = { -> }
        )
    }
}