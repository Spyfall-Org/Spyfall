package com.dangerfield.features.gameplay.internal.singledevice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text

@Composable
fun EndOrGoBackDialog(
    onDismissRequest: () -> Unit,
    onEndGame: () -> Unit,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    PageLogEffect(
        route = route("end_game_or_go_back_dialog"),
        type = PageType.Dialog
    )

    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(text = "End the game or go back?")
        },
        content = {
            Text(text = "Going back will show the previous players details, ending will delete the game.")
        },
        bottomContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEndGame,
                    type = ButtonType.Accent
                ) {
                    Text(text = "End Game")
                }

                VerticalSpacerS800()

                Button(
                    type = ButtonType.Regular,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGoBack
                ) {
                    Text(text = "Go Back")
                }

                VerticalSpacerS800()

                Button(
                    style = ButtonStyle.NoBackground,
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
private fun SEndOrGoBackDialogPreview() {
    PreviewContent {
        EndOrGoBackDialog(
            onDismissRequest = { -> },
            onEndGame = { -> },
            onGoBack = { -> }
        )
    }
}