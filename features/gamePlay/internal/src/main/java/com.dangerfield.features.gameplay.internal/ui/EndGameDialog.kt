package com.dangerfield.features.gameplay.internal.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.oddoneoout.features.gameplay.internal.R

@Composable
fun GamePlayLeaveDialog(
    onDismissRequest: () -> Unit,
    onLeaveConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {

    PageLogEffect(
        route = route("game_play_leave_dialog"),
        type = PageType.Dialog
    )

    // TODO cleanup investiage color button on top or bottom and negative vs positive
    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.gamePlay_leaveDialog_header))
        },
        content = {
            Text(text = dictionaryString(R.string.gamePlay_leaveDialog_body))
        },
        bottomContent = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLeaveConfirmed,
                    type = ButtonType.Primary
                ) {
                    Text(text = dictionaryString(R.string.gamePlay_leaveDialogLeave_action))
                }

                VerticalSpacerD800()

                Button(
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = dictionaryString(R.string.gamePlay_leaveGameDialogCancel_action))
                }
            }
        }
    )
}

@Composable
@Preview
private fun WaitingRoomLeavingDialogPreview() {
    Preview {
        GamePlayLeaveDialog(
            onDismissRequest = { -> },
            onLeaveConfirmed = { -> }
        )
    }
}