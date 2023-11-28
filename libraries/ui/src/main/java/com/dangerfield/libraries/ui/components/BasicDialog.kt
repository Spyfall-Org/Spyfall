package com.dangerfield.libraries.ui.components


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.modifiers.drawVerticalScrollbar
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
fun BasicDialog(
    onDismiss: () -> Unit,
    title: String,
    description: String,
    primaryButtonText: String,
    modifier: Modifier = Modifier,
    secondaryButtonText: String? = null,
    onPrimaryButtonClicked: () -> Unit,
    onSecondaryButtonClicked: (() -> Unit)? = null,
) {
    val scrollState = rememberScrollState()

    Box(
        Modifier
            .fillMaxSize()
            .background(SpyfallTheme.colorScheme.backgroundOverlay.color)
    ) {
        Dialog(
            onDismissRequest = onDismiss,
        ) {
            Column(
                modifier = modifier
                    .background(
                        SpyfallTheme.colorScheme.surfacePrimary.color,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(
                        top = Spacing.S800,
                        start = Spacing.S800,
                        end = Spacing.S800,
                        bottom = Spacing.S800
                    )
            ) {
                Text(
                    text = title,
                    typographyToken = SpyfallTheme.typography.Heading.H900,
                )

                Spacer(modifier = Modifier.height(Spacing.S600))

                Text(
                    typographyToken = SpyfallTheme.typography.Body.B700,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .drawVerticalScrollbar(
                            scrollState,
                            SpyfallTheme.colorScheme.textDisabled.color
                        )
                        .verticalScroll(scrollState),
                    text = description,
                )

                Spacer(modifier = Modifier.height(Spacing.S1000))

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
            }

            Spacer(modifier = Modifier.height(Spacing.S1000))
        }
    }
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
