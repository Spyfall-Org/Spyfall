package com.dangerfield.features.gameplay.internal.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.iconTopAccessory
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.oddoneoout.features.gameplay.internal.R

@Composable
fun GameHelpBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    onDismiss: (BottomSheetState) -> Unit
) {

    BasicBottomSheet(
        onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        topAccessory = iconTopAccessory(icon = SpyfallIcon.Question(null)),
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.gamePlayHelp_howToPlay_header))
        },
        content = {
            Column {
                // TODO MVP update the language here
                Text(text = "Read The Rules Bitch")
            }
        },
        bottomContent = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.S800),
                onClick = { onDismiss(bottomSheetState) }
            ) {
                Text(text = dictionaryString(id = R.string.app_okay_action))
            }
        }
    )
}

@Composable
@ThemePreviews
private fun PreviewGameHelpBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    PreviewContent {
        GameHelpBottomSheet(
            bottomSheetState = bottomSheetState,
            onDismiss = { }

        )
    }
}