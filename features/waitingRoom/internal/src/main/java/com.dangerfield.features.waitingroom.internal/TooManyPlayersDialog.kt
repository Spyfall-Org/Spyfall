package com.dangerfield.features.waitingroom.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.PreviewContent
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.oddoneoout.features.waitingroom.internal.R

@Composable
fun TooManyPlayersDialog(
    onDismissRequest: () -> Unit,
    maxPlayers: Int,
    playersSize: Int,
    modifier: Modifier = Modifier
) {

    PageLogEffect(
        route = route("waiting_room_too_many_players_dialog"),
        type = PageType.Dialog
    )

    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.app_oops_text))
        },
        content = {
            Text(
                text = dictionaryString(
                    R.string.waitingRoom_tooManyPlayersDialog_body,
                    "max" to maxPlayers.toString(),
                    "numToLeave" to (playersSize - maxPlayers).toString()
                )
            )
        },
        bottomContent = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Button(
                    type = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = dictionaryString(R.string.app_okay_action))
                }
            }
        }
    )
}

@Composable
@Preview
private fun TooManyPlayersDialogPreview() {
    PreviewContent {
        TooManyPlayersDialog(
            onDismissRequest = { -> },
            maxPlayers = 3,
            playersSize = 4,
        )
    }
}