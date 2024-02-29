package com.dangerfield.features.gameplay.internal.singledevice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.oddoneoout.features.gameplay.internal.R

@Composable
fun EndGameDialog(
    onDismissRequest: () -> Unit,
    onEndGame: () -> Unit,
    modifier: Modifier = Modifier
) {

    PageLogEffect(
        route = route("end_game_dialog"),
        type = PageType.Dialog
    )

    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.singleDevice_endGameDialog_header))
        },
        content = {
            Text(text = dictionaryString(R.string.singleDevice_endGameDialogDescription_text))
        },
        bottomContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEndGame,
                    type = ButtonType.Primary
                ) {
                    Text(text = dictionaryString(R.string.singleDeviceEndGameDialog_end_action))
                }

                VerticalSpacerD800()

                Button(
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = dictionaryString(R.string.singleDeviceEndGameDialog_cancel_action))
                }
            }
        }
    )
}

@Composable
@Preview
private fun SingleDeviceEndGameDialogPreview() {
    Preview {
        EndGameDialog(
            onDismissRequest = { -> },
            onEndGame = { -> }
        )
    }
}