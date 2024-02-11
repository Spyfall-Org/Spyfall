package com.dangerfield.features.gameplay.internal.voting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
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
import com.dangerfield.libraries.ui.PreviewContent
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
        topAccessory = iconTopAccessory(icon = SpyfallIcon.Alarm(null)),
        modifier = modifier,
        topContent = {
            Text(text = if (hasVoted) {
                dictionaryString(R.string.votingRules_alreadyVoted_header)
            } else {
                dictionaryString(R.string.votingRules_notVoted_header)
            }
            )
        },
        content = { VotingExplanationBlock(hasVoted) },
        bottomContent = {
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
            VerticalSpacerS800()
        }

        val rulePoints = dictionaryString(id = R.string.votingRules_rulePoints_text).split("\n")

        rulePoints.forEach {
            BulletRow {
                Text(text = it)
            }

            VerticalSpacerS500()
        }
    }
}

@Composable
@Preview
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