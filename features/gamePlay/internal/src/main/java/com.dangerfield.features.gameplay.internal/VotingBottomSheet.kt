package com.dangerfield.features.gameplay.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS500
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
fun VotingBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    hasVoted: Boolean,
    onDismiss: (BottomSheetState) -> Unit
) {

    BasicBottomSheet(
        onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        topAccessory = iconTopAccessory(icon = SpyfallIcon.Alarm(null)),
        modifier = modifier,
        topContent = {
            Text(text = if (hasVoted) "Voting Rules" else "Time to vote!")
        },
        content = {
            Column {
                Text(text = "You can choose to do this through the app by voting or amongst yourselves by talking")

                VerticalSpacerS800()

                BulletRow {
                    Text(text = "Come to a majority agreement on who you all think the odd one out is")
                }

                VerticalSpacerS500()

                BulletRow {
                    Text(text = "Once done (regardless of if the guess is correct) the odd one out must reveal themselves and guess the location")
                }

                VerticalSpacerS500()

                BulletRow {
                    Text(text = "If the majority of the players guess the odd one out correctly the players win")
                }

                VerticalSpacerS500()

                BulletRow {
                    Text(text = "If the odd one out guesses the location correctly the odd one out wins")
                }

                VerticalSpacerS500()
                
                BulletRow {
                    Text(text = "If both sides win or both sides lose then the game ends in a draw!")
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
private fun PreviewVotingBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    PreviewContent {
        VotingBottomSheet(
            bottomSheetState = bottomSheetState,
            onDismiss = { },
            hasVoted = false
        )
    }
}