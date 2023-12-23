package com.dangerfield.features.settings.internal.contactus

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.modal.BasicDialog

@Composable
fun ContactUsErrorDialog(
    onDismiss: () -> Unit,
) {

    val title = "Lets try that again"

    val description = "Looks like something went wrong submitting your form, please try again."

    BasicDialog(
        onDismissRequest = onDismiss,
        title = title,
        description = description,
        primaryButtonText = "Ok",
        onPrimaryButtonClicked = {
            onDismiss()
        }
    )
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogUpdate() {
    PreviewContent {
        ContactUsErrorDialog(
            onDismiss = {},
        )
    }
}

