package com.dangerfield.libraries.dictionary.internal.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.PreviewContent

@Composable
fun LanguageSupportDialog(
    language: String,
    isUnsupported: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = if (isUnsupported) {
        "Unfortunately we do not have support for $language yet.\n\nIf you would like to help us translate and add support for this language please reach out to us using the contact form in settings. Thank you!"
    } else {
        "We are currently working on improving our support for $language.\n\nSome of the words you see may not be perfectly translated. If you see any mistakes please let us know by using the contact form in settings. Thank you!"
    }

    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        topContent = {
            Text(text = "Do you speak $language?")
        },
        content = {
            Text(text = message)
        },
        bottomContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    type = ButtonType.Accent,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(text = "Okay")
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
        isDarkMode = true
    ) {
        LanguageSupportDialog(
            onDismissRequest = { -> },
            language = "Dutch",
            isUnsupported = false
        )
    }
}