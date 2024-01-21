package com.dangerfield.features.gameplay.internal.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS100
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
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
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
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
            ScrollingColumnWithFadingEdge {

                Body(text = dictionaryString(R.string.rules_gamePlayWhatsVisible_header))

                VerticalSpacerS500()

                val whatsVisibleBullets =
                    dictionaryString(R.string.rules_gamePlayWhatsVisibleBullets_text).split("\n")

                whatsVisibleBullets.forEach {
                    BulletRow {
                        Body(text = it)
                    }
                }

                VerticalSpacerS500()

                SubTitle(text = dictionaryString(R.string.rules_theGoalSection_header))

                val goalBullets = dictionaryString(R.string.rules_goalBullets_text).split("\n")
                goalBullets.forEach {
                    Body(text = it)
                    VerticalSpacerS500()
                }

                SubTitle(text = dictionaryString(R.string.rules_questioningSection_header))

                Body(text = dictionaryString(R.string.rules_questioningSection_body))

                VerticalSpacerS800()

                SubTitle(text = dictionaryString(R.string.rules_howToWinSection_header))

                val howToWinBullets =
                    dictionaryString(R.string.rules_howToWinSectionBullets_text).split("\n")

                howToWinBullets.forEach {
                    BulletRow {
                        Body(text = it)
                    }

                    VerticalSpacerS500()
                }

                VerticalSpacerS1200()
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
private fun SubTitle(text: String) {
    Column {
        Text(text = text, typographyToken = OddOneOutTheme.typography.Body.B700.Bold)
        VerticalSpacerS100()
    }
}

@Composable
private fun Body(text: String) {
    Column {
        Text(text = text, typographyToken = OddOneOutTheme.typography.Body.B600)
    }
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