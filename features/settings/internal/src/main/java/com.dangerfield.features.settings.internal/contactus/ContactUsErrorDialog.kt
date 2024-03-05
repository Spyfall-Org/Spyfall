package com.dangerfield.features.settings.internal.contactus

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.oddoneoout.features.settings.internal.R

@Composable
fun ContactUsErrorDialog(
    onDismiss: () -> Unit,
) {

    val title = dictionaryString(R.string.app_tryAgainErrorDialog_header)

    val description = dictionaryString(R.string.contactUs_errorSubmitting_body)

    BasicDialog(
        onDismissRequest = onDismiss,
        title = title,
        description = description,
        primaryButtonText = dictionaryString(id = R.string.app_okay_action),
        onPrimaryButtonClicked = {
            onDismiss()
        }
    )
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogUpdate() {
    Preview {
        ContactUsErrorDialog(
            onDismiss = {},
        )
    }
}

