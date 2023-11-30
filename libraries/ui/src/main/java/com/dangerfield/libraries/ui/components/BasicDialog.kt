package com.dangerfield.libraries.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.Text
import spyfallx.ui.Spacing

@Composable fun BasicDialog(
    onDismiss: () -> Unit,
    title: String,
    description: String,
    primaryButtonText: String,
    modifier: Modifier = Modifier,
    secondaryButtonText: String? = null,
    onPrimaryButtonClicked: () -> Unit,
    onSecondaryButtonClicked: (() -> Unit)? = null,
) {
    Dialog(
        onDismiss = onDismiss,
        modifier = modifier,
        topContent = { Text(text = title) },
        content = { Text(text = description) },
        bottomContent = {
            Column(
                modifier = Modifier.padding(horizontal = Spacing.S1000)
            ) {
                Button(
                    size = ButtonSize.Small,
                    onClick = onPrimaryButtonClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = primaryButtonText)
                }

                if (secondaryButtonText != null && onSecondaryButtonClicked != null) {

                    Spacer(modifier = Modifier.height(Spacing.S600))

                    Button(
                        size = ButtonSize.Small,
                        type = ButtonType.Regular,
                        onClick = onSecondaryButtonClicked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = secondaryButtonText)
                    }
                }
            }
        },
    )
}

@Composable
@Preview
private fun PreviewBasicDialog() {
    PreviewContent {
        BasicDialog(
            onDismiss = { -> },
            title = "This is a title",
            description = "this is a description, pretty cool right? ",
            primaryButtonText = "No",
            secondaryButtonText = "Yes",
            onPrimaryButtonClicked = {},
            onSecondaryButtonClicked = {}
        )
    }
}

@Composable
@Preview
@Suppress("MagicNumber")
private fun PreviewBasicDialogLongDescription() {
    PreviewContent {
        BasicDialog(
            onDismiss = { -> },
            title = "This is a title",
            description = "this is a description thats super long.".repeat(50),
            primaryButtonText = "No",
            secondaryButtonText = "Yes",
            onPrimaryButtonClicked = {},
            onSecondaryButtonClicked = {}
        )
    }
}
