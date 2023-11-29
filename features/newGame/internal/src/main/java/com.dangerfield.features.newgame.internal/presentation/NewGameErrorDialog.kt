package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.runtime.Composable
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.components.BasicDialog

@Composable
fun NewGameErrorDialog(
    onDismiss: () -> Unit
) {

    BasicDialog(
        onDismiss = onDismiss,
        title = "Something went wrong",
        description = "We're sorry, something seems to have gone wrong creating this game, please try again",
        primaryButtonText = "Ok",
        onPrimaryButtonClicked =  onDismiss
    )
}

@Composable
@ThemePreviews
private fun PreviewNewGameErrorDialog() {
    PreviewContent {
        NewGameErrorDialog(
            onDismiss = {},
        )
    }
}

