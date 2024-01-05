package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.runtime.Composable
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.components.modal.BasicDialog

@Composable
fun NewGameErrorDialog(
    onDismissRequest: () -> Unit
) {

    // TODO this is poping up when anything at all goes wrong on new game screen. Eitehr chagne the message or make the message dynamic
    BasicDialog(
        onDismissRequest = onDismissRequest,
        title = "Something went wrong",
        description = "We're sorry, something seems to have gone wrong creating this game, please try again",
        primaryButtonText = "Ok",
        onPrimaryButtonClicked =  onDismissRequest
    )
}

@Composable
@ThemePreviews
private fun PreviewNewGameErrorDialog() {
    PreviewContent {
        NewGameErrorDialog(
            onDismissRequest = {},
        )
    }
}

