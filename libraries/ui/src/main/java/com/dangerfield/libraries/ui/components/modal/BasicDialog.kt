package com.dangerfield.libraries.ui.components.modal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.clip
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.color.background

@Composable
fun BasicDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OddOneOutTheme.colorScheme.backgroundOverlay)
    )
    {

        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            Column(
                modifier = Modifier
                    .clip(Radii.Card)
                    .background(OddOneOutTheme.colorScheme.background)
            ) {

                ModalContent(
                    modifier = modifier.padding(
                        top = Spacing.S800,
                        start = Spacing.S800,
                        end = Spacing.S800,
                        bottom = Spacing.S800
                    ),
                    topContent = topContent,
                    content = content,
                    bottomContent = bottomContent
                )
            }
        }
    }
}

@Composable
fun BasicDialog(
    onDismissRequest: () -> Unit,
    title: String,
    description: String,
    primaryButtonText: String,
    modifier: Modifier = Modifier,
    secondaryButtonText: String? = null,
    onPrimaryButtonClicked: () -> Unit,
    onSecondaryButtonClicked: (() -> Unit)? = null,
) {
    BasicDialog(
        onDismissRequest = onDismissRequest,
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
@ThemePreviews
private fun PreviewDialog() {
    PreviewContent {
        BasicDialog(
            onDismissRequest = { -> },
            modifier = Modifier,
            topContent = { Text(text = "Top Content") },
            content = {
                Column {
                    Text(text = "content".repeat(10))
                    Text(text = "is good".repeat(10))
                }
            },
            bottomContent = {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Bottom Content")
                }
            },
        )
    }
}


@Composable
@Preview
private fun PreviewBasicDialog() {
    PreviewContent {
        BasicDialog(
            onDismissRequest = { -> },
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
            onDismissRequest = { -> },
            title = "This is a title",
            description = "this is a description thats super long.".repeat(50),
            primaryButtonText = "No",
            secondaryButtonText = "Yes",
            onPrimaryButtonClicked = {},
            onSecondaryButtonClicked = {}
        )
    }
}
