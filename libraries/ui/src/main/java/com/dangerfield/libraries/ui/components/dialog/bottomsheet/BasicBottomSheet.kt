package com.dangerfield.libraries.ui.components.dialog.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.dialog.ModalContent
import com.dangerfield.libraries.ui.components.text.Text

@Composable
fun BasicBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: BottomSheetState = rememberBottomSheetState(),
    showCloseButton: Boolean = false,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    stickyTopContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    stickyBottomContent: @Composable (() -> Unit)? = null,
) {
    BottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier,
        state = state,
        contentAlignment = contentAlignment,
    ) {
        Column(
            modifier = Modifier.padding(
                start = Dimension.D800,
                end = Dimension.D800,
                bottom = Dimension.D800
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
                VerticalSpacerD800()
            }

            ModalContent(
                modifier = modifier,
                topContent = stickyTopContent,
                content = content,
                bottomContent = stickyBottomContent,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewBasicBottomSheetCloseButton() {
    Preview() {
        BasicBottomSheet(
            state = rememberBottomSheetState(BottomSheetValue.Expanded),
            onDismissRequest = { -> },
            modifier = Modifier,
            showCloseButton = true,
            stickyTopContent = { Text(text = "Top Content") },
            content = {
                Column {
                    Text(text = "content".repeat(100))
                    Text(text = "is good".repeat(100))
                }
            },
            stickyBottomContent = {
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
    Preview {
        BasicBottomSheet(
            state = rememberBottomSheetState(BottomSheetValue.Expanded),
            onDismissRequest = { -> },
            modifier = Modifier,
            stickyTopContent = { Text(text = "Top Content") },
            content = {
                Column {
                    Text(text = "content".repeat(10))
                    Text(text = "is good".repeat(10))
                }
            },
            stickyBottomContent = {
                Button(onClick = { }) {
                    Text(text = "Bottom Content")
                }
            },
        )
    }
}