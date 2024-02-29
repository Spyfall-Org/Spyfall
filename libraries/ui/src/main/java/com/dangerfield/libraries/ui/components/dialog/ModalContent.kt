package com.dangerfield.libraries.ui.components.dialog

import androidx.compose.foundation.background
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
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ProvideButtonConfig
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun ModalContent(
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable (() -> Unit)? = null,
) {

    Column(
        modifier = modifier
            .background(OddOneOutTheme.colors.background.color)
    ) {
        ProvideTextConfig(OddOneOutTheme.typography.Heading.H900) {
            topContent()
        }

        Spacer(modifier = Modifier.height(Dimension.D600))

        ProvideTextConfig(OddOneOutTheme.typography.Body.B700) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)

            ) {
                content()
            }
        }

        if (bottomContent != null) {
            Spacer(modifier = Modifier.height(Dimension.D1000))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimension.D1000),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProvideButtonConfig(size = ButtonSize.Small) {
                    bottomContent()
                }
            }

            Spacer(modifier = Modifier.height(Dimension.D800))
        }
    }
}

@Composable
@Preview
private fun PreviewModalContent() {
    Preview {
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

@Composable
@Preview
private fun PreviewModalContentLong() {
    Preview {
        ModalContent(
            modifier = Modifier,
            topContent = { Text(text = "Top Content") },
            content = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(text = "This is a bunch of words that take sus space".repeat(100))
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