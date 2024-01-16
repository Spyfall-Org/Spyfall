package com.dangerfield.features.gameplay.internal.singledevice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dangerfield.features.gameplay.internal.voting.singleDeviceVotingResultsRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text

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
            Text(text = "Ending so soon?")
        },
        content = {
            Text(text = "Going back will delete the game, are you sure you want to continue?")
        },
        bottomContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEndGame,
                    type = ButtonType.Accent
                ) {
                    Text(text = "End")
                }

                VerticalSpacerS800()

                Button(
                    type = ButtonType.Regular,
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
private fun SingleDeviceEndGameDialogPreview() {
    PreviewContent {
        EndGameDialog(
            onDismissRequest = { -> },
            onEndGame = { -> }
        )
    }
}