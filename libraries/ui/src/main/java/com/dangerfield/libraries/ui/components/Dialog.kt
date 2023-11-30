package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ProvideButtonConfig
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.modifiers.drawVerticalScrollbar
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.ui.Spacing

@Composable
fun Dialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        DialogContent(
            modifier = modifier,
            topContent = topContent,
            content = content,
            bottomContent = bottomContent
        )
    }
}

@Composable
private fun DialogContent(
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
) {
    val scrollState = rememberScrollState()

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
        ProvideTextConfig(SpyfallTheme.typography.Heading.H900) {
            topContent()
        }

        Spacer(modifier = Modifier.height(Spacing.S600))

        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .drawVerticalScrollbar(
                    scrollState,
                    SpyfallTheme.colorScheme.textDisabled.color
                )
                .verticalScroll(scrollState),
        ) {

            ProvideTextConfig(SpyfallTheme.typography.Body.B700) {
                content()
            }
        }


        Spacer(modifier = Modifier.height(Spacing.S1000))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.S1000),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProvideButtonConfig(size = ButtonSize.Small) {
                bottomContent()
            }
        }
    }

    Spacer(modifier = Modifier.height(Spacing.S1000))
}


@Composable
@Preview
private fun PreviewDialogContent() {
    PreviewContent {
        DialogContent(
            modifier = Modifier,
            topContent = { Text(text = "Top Content") },
            content = { Text(text = "context".repeat(50)) },
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
private fun PreviewDialog() {
    PreviewContent {
        Dialog(
            onDismiss = { -> },
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


