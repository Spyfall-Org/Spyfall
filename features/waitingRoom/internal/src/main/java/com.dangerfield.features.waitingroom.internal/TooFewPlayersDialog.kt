package com.dangerfield.features.waitingroom.internal

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
import com.dangerfield.oddoneoout.features.waitingroom.internal.R

@Composable
fun TooFewPlayersDialog(
    onDismissRequest: () -> Unit,
    onStartConfirmed: () -> Unit,
    minPlayers: Int,
    modifier: Modifier = Modifier
) {

    PageLogEffect(
        route = route("waiting_room_too_few_players_dialog"),
        type = PageType.Dialog
    )

    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.waitingRoom_tooFewPlayersDialog_header))
        },
        content = {
            Text(
                text = dictionaryString(
                    R.string.waitingRoom_tooFewPlayersDialog_body,
                    "min" to minPlayers.toString()
                )
            )
        },
        bottomContent = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStartConfirmed,
                    type = ButtonType.Primary
                ) {
                    Text(text = dictionaryString(R.string.waitingRoom_confirmStart_action))
                }

                VerticalSpacerD800()

                Button(
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = dictionaryString(R.string.app_cancel_action))
                }
            }
        }
    )
}

@Composable
@Preview
private fun StartConfirmationDialogPreview() {
    Preview {
        TooFewPlayersDialog(
            onDismissRequest = { -> },
            onStartConfirmed = { -> },
            minPlayers = 3
        )
    }
}