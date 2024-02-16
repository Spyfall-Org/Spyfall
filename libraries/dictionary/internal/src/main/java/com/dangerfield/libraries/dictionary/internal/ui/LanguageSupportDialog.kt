package com.dangerfield.libraries.dictionary.internal.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.oddoneoout.libraries.dictionary.internal.R

@Composable
fun LanguageSupportDialog(
    language: String,
    isUnsupported: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = if (isUnsupported) {
        dictionaryString(
            R.string.languageSupport_unsupportedDialog_body,
            "language" to language
        )
    } else {
        dictionaryString(
            R.string.languageSupport_partialSupportDialog_body,
            "language" to language
        )
    }

    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(
                text = dictionaryString(
                    R.string.languageSupport_dialog_header,
                    "language" to language
                )
            )
        },
        content = {
            Text(text = message)
        },
        bottomContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    type = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = dictionaryString(id = R.string.app_okay_action))
                }
            }
        }
    )
}

@Preview
@Composable
private fun LanguageSupportDialogPreview() {
    PreviewContent {
        LanguageSupportDialog(
            onDismissRequest = { -> },
            language = "Spanish",
            isUnsupported = true
        )
    }
}

@Preview
@Composable
private fun LanguageSupportDialogPartialPreview() {
    PreviewContent(
    ) {
        LanguageSupportDialog(
            onDismissRequest = { -> },
            language = "Dutch",
            isUnsupported = false
        )
    }
}