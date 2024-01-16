package com.dangerfield.libraries.ui.components.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ProvideButtonConfig
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.color.background

@Composable
fun ModalContent(
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable (() -> Unit)? = null,
) {

    Column(
        modifier = modifier
            .background(OddOneOutTheme.colorScheme.background)
    ) {
        ProvideTextConfig(OddOneOutTheme.typography.Heading.H900) {
            topContent()
        }

        Spacer(modifier = Modifier.height(Spacing.S600))

        ProvideTextConfig(OddOneOutTheme.typography.Body.B700) {
            content()
        }

        if (bottomContent != null) {
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
    }
}

@Composable
@Preview
private fun PreviewModalContent() {
    PreviewContent {
        ModalContent(
            modifier = Modifier,
            topContent = { Text(text = "Top Content") },
            content = {
                Column {
                    Text(text = "context".repeat(50))
                }
            },
            bottomContent = {
                Button(onClick = { }) {
                    Text(text = "Bottom Content")
                }
            },
        )
    }
}