package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.runtime.Composable
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.components.modal.BasicDialog

@Composable
fun NewGameErrorDialog(
    onDismissRequest: () -> Unit
) {

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

