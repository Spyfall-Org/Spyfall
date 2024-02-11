@file:Suppress("MaxLineLength")
package com.dangerfield.features.rules.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS100
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.dragHandleTopAccessory
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.PreviewContent
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.rules.internal.R

@Composable
fun RulesBottomSheet(
    onDismissRequest: (BottomSheetState) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: BottomSheetState = rememberBottomSheetState(),
) {
    BasicBottomSheet(
        onDismissRequest = { onDismissRequest(sheetState) },
        modifier = modifier,
        state = sheetState,
        showCloseButton = true,
        topAccessory = dragHandleTopAccessory(),
        topContent = {
            Text(text = dictionaryString(R.string.rules_bottomSheet_header))
        },
        content = {
            RulesContent()
        }
    )
}

@Composable
private fun RulesContent() {
    ScrollingColumnWithFadingEdge(
        Modifier.padding(horizontal = Spacing.S800)
    ) {
        Body(text = dictionaryString(R.string.rules_tldr_text))
        VerticalSpacerS800()

        val overviewTexts = dictionaryString(R.string.rules_overviewSectionParts_text).split("\n")
        Section(title = dictionaryString(R.string.rules_overview_header)) {
            overviewTexts.forEachIndexed { index, it ->
                Body(text = it)
                if (index != overviewTexts.lastIndex) {
                    VerticalSpacerS500()
                }
            }
        }

        Section(title = dictionaryString(R.string.rules_gettingStartedSection_header)) {
            SubTitle(dictionaryString(R.string.rules_joinAGameSection_header))

            val joinAGameBullets = dictionaryString(R.string.rules_joinAGameBullets_text).split("\n")
            joinAGameBullets.forEach { s ->
                BulletRow {
                    Body(text = s)
                }
                VerticalSpacerS500()
            }

            VerticalSpacerS500()

            SubTitle(dictionaryString(R.string.rules_createAGameSection_header))

            val createAGameBullets = dictionaryString(R.string.rules_createAGameBullets_text).split("\n")
            createAGameBullets.forEach {
                BulletRow {
                    Body(text = it)
                }
            }
        }

        Section(title = dictionaryString(R.string.rules_gamePlaySection_header)) {

            Body(text = dictionaryString(R.string.rules_gamePlayWhatsVisible_header))

            VerticalSpacerS500()

            val whatsVisibleBullets = dictionaryString(R.string.rules_gamePlayWhatsVisibleBullets_text).split("\n")

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

            val howToWinBullets = dictionaryString(R.string.rules_howToWinSectionBullets_text).split("\n")

            howToWinBullets.forEach {
                BulletRow {
                    Body(text = it)
                }

                VerticalSpacerS500()
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Title(text = title)
        VerticalSpacerS800()
        content()
        VerticalSpacerS1200()
    }
}

@Composable
private fun Title(text: String) {
    Column {
        Text(text = text, typographyToken = OddOneOutTheme.typography.Heading.H800)
    }
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
@Preview
private fun PreviewRulesScreen() {
    PreviewContent {
        val sheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
        RulesBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {}
        )
    }
}