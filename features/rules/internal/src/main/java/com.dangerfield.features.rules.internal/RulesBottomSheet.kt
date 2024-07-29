package com.dangerfield.features.rules.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.features.gameplay.RoleCard
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD100
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.rules.internal.R

@Composable
fun RulesBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    onDismiss: (BottomSheetState) -> Unit
) {

    BasicBottomSheet(
        onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        modifier = modifier,
        stickyTopContent = {
            Text(text = dictionaryString(R.string.gamePlayHelp_howToPlay_header))
        },
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                BulletRow {
                    Body(text = dictionaryString(R.string.rules_pointOne_text))
                }

                RoleCard(
                    modifier = Modifier.scale(0.6f),
                    role = dictionaryString(R.string.rules_roleExample_text),
                    text = dictionaryString(id = R.string.gamePlay_playerRoleTip_text),
                    packItem = PackItem.Location(name = dictionaryString(R.string.rules_locationExample_text), roles = emptyList()),
                    isTheOddOneOut = false,
                    isVisible = true,
                    onHideShowClicked = { -> },
                )

                BulletRow {
                    Body(text = dictionaryString(R.string.rules_pointTwo_text))
                }

                VerticalSpacerD800()

                Image(
                    painter = painterResource(id = R.drawable.ic_people_odd_one_out),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                VerticalSpacerD800()

                BulletRow {
                    Body(text = dictionaryString(R.string.rules_pointThree_text))
                }


                VerticalSpacerD800()

                Image(
                    painter = painterResource(id = R.drawable.ic_people_question),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                VerticalSpacerD800()

                BulletRow {
                    Body(text = dictionaryString(R.string.rules_pointFour_text))
                }

                VerticalSpacerD500()

                BulletRow {
                    Body(text = dictionaryString(R.string.rules_pointFive_text))
                }

                VerticalSpacerD1200()

                SubTitle(text = dictionaryString(R.string.rules_howToWinSection_header))

                VerticalSpacerD500()
                val howToWinBullets =
                    dictionaryString(R.string.rules_howToWinSectionBullets_text).split("\n")

                howToWinBullets.forEach {
                    BulletRow {
                        Body(text = it)
                    }

                    VerticalSpacerD500()
                }

                VerticalSpacerD1200()
            }
        },
        stickyBottomContent = {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
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
        Text(text = text, typography = OddOneOutTheme.typography.Body.B700.Bold)
        VerticalSpacerD100()
    }
}

@Composable
private fun Body(text: String) {
    Column {
        Text(text = text, typography = OddOneOutTheme.typography.Body.B600)
    }
}

@Composable
@Preview
private fun PreviewRulesBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    Preview {
        RulesBottomSheet(
            bottomSheetState = bottomSheetState,
            onDismiss = { }

        )
    }
}