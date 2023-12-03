package com.dangerfield.libraries.ui.components.modal.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.modal.ModalContent
import com.dangerfield.libraries.ui.components.text.Text

@Composable
fun BasicBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: BottomSheetState = rememberBottomSheetState(),
    topAccessory: TopAccessory = dragHandleTopAccessory(),
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    topContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
) {
    BottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier,
        state = state,
        topAccessory = topAccessory,
        contentAlignment = contentAlignment,
    ) {
        VerticalSpacerS800()
        ModalContent(
            modifier = modifier,
            topContent = topContent,
            content = content,
            bottomContent = bottomContent,
        )
    }
}

@Composable
@ThemePreviews
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