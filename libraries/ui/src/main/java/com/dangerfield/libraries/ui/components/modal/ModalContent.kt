package com.dangerfield.libraries.ui.components.modal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ProvideButtonConfig
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.modifiers.drawVerticalScrollbar
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.ui.Spacing
import spyfallx.ui.color.background

@Composable
fun ModalContent(
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .background(SpyfallTheme.colorScheme.background)
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
private fun PreviewModalContent() {
    PreviewContent {
        ModalContent(
            modifier = Modifier,
            topContent = { Text(text = "Top Content") },
            content = { Text(text = "context".repeat(50)) },
            bottomContent = {
                Button(onClick = { }) {
                    Text(text = "Bottom Content")
                }
            },
        )
    }
}