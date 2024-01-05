package com.dangerfield.libraries.ui.components.modal.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.modal.ModalContent
import com.dangerfield.libraries.ui.components.text.Text

@Composable
fun BasicBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: BottomSheetState = rememberBottomSheetState(),
    topAccessory: TopAccessory = dragHandleTopAccessory(),
    showCloseButton: Boolean = false,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable (() -> Unit)? = null,
) {
    BottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier,
        state = state,
        topAccessory = topAccessory,
        contentAlignment = contentAlignment,
    ) {
        Column(
            modifier = Modifier.padding(
                top = Spacing.S800,
                start = Spacing.S800,
                end = Spacing.S800,
                bottom = Spacing.S800
            )
        ) {
            if (showCloseButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        icon = SpyfallIcon.Close("CLose"),
                        onClick = onDismissRequest
                    )
                }
            } else {
                VerticalSpacerS800()
            }

            ModalContent(
                modifier = modifier,
                topContent = topContent,
                content = content,
                bottomContent = bottomContent,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewBasicBottomSheetCloseButton() {
    PreviewContent(isDarkMode = true) {
        BasicBottomSheet(
            state = rememberBottomSheetState(BottomSheetValue.Expanded),
            onDismissRequest = { -> },
            modifier = Modifier,
            showCloseButton = true,
            topContent = { Text(text = "Top Content") },
            content = {
                Column {
                    Text(text = "content".repeat(10))
                    Text(text = "is good".repeat(10))
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
private fun PreviewBasicBottomSheet() {
    PreviewContent {
        BasicBottomSheet(
            state = rememberBottomSheetState(BottomSheetValue.Expanded),
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
                Button(onClick = { }) {
                    Text(text = "Bottom Content")
                }
            },
        )
    }
}