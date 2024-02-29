package com.dangerfield.features.gameplay.internal.voting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.oddoneoout.features.gameplay.internal.R

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
        modifier = modifier,
        stickyTopContent = {
            Text(text = if (hasVoted) {
                dictionaryString(R.string.votingRules_alreadyVoted_header)
            } else {
                dictionaryString(R.string.votingRules_notVoted_header)
            }
            )
        },
        content = { VotingExplanationBlock(hasVoted) },
        stickyBottomContent = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDismiss(bottomSheetState) }
            ) {
                Text(text = dictionaryString(id = R.string.app_okay_action))
            }
        }
    )
}

@Composable
fun VotingExplanationBlock(hasVoted: Boolean = false) {
    Column {

        if (!hasVoted) {
            Text(text = dictionaryString(R.string.votingRules_whereToVote_header))
            VerticalSpacerD800()
        }

        val rulePoints = dictionaryString(id = R.string.votingRules_rulePoints_text).split("\n")

        rulePoints.forEach {
            BulletRow {
                Text(text = it)
            }

            VerticalSpacerD500()
        }
    }
}

@Composable
@Preview
private fun PreviewVotingBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    Preview {
        VotingBottomSheet(
            bottomSheetState = bottomSheetState,
            onDismiss = { },
            hasVoted = false
        )
    }
}