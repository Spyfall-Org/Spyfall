package com.dangerfield.features.gameplay.internal.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.iconTopAccessory
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text

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
            Text(text = "How to play")
        },
        content = {
            Column {
                // TODO update the language here
                Text(text = "Read The Rules Bitch")

                VerticalSpacerS800()

                BulletRow {
                    Text(text = "Come to a consensus on who you all think the odd one out is")
                }

                BulletRow {
                    Text(text = "Once done (regardless of if the guess is correct) the odd one out can reveal themselves and guess the location")
                }

                BulletRow {
                    Text(text = "If the players guess the odd one out correctly the player wins")
                }

                BulletRow {
                    Text(text = "If the odd one out guesses the location correctly the odd one out wins")
                }

                BulletRow {
                    Text(text = "If both guess correctly or both guess incorrectly the game ends in a draw! But congrats to the odd one out for fooling everyone!")
                }
            }
        },
        bottomContent = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.S800),
                onClick = { onDismiss(bottomSheetState) }
            ) {
                Text(text = "Okay")
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