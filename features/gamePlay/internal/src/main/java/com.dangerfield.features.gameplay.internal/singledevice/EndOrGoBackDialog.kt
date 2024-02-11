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
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.PreviewContent
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.oddoneoout.features.gameplay.internal.R

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
            Text(text = dictionaryString(R.string.endOrGoBack_dialog_header))
        },
        content = {
            Text(text = dictionaryString(R.string.endOrGoBack_description_text))
        },
        bottomContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEndGame,
                    type = ButtonType.Primary
                ) {
                    Text(text = dictionaryString(R.string.app_end_game_action))
                }

                VerticalSpacerS800()

                Button(
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGoBack
                ) {
                    Text(text = dictionaryString(R.string.endOrGoBack_goBack_action))
                }

                VerticalSpacerS800()

                Button(
                    style = ButtonStyle.NoBackground,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = dictionaryString(R.string.endOrGoBack_cancel_action))
                }
            }
        }
    )
}

@Composable
@Preview
private fun SEndOrGoBackDialogPreview() {
    PreviewContent {
        EndOrGoBackDialog(
            onDismissRequest = { -> },
            onEndGame = { -> },
            onGoBack = { -> }
        )
    }
}