package com.dangerfield.features.gameplay.internal.singledevice.gameplay

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.features.ads.ui.AdBanner
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.gameplay.internal.singledevice.EndGameDialog
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.gameplay.internal.R

// TODO consider making examples backend driven or rotating
@Composable
fun SingleDeviceGamePlayScreen(
    timeRemaining: String,
    isTimeUp: Boolean,
    modifier: Modifier = Modifier,
    onTimeToVote: () -> Unit = {},
    onRestartGameClicked: () -> Unit = {},
    onEndGameClicked: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    var shouldShowExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        shouldShowExitDialog = !shouldShowExitDialog
    }

    Box {
        GamePlayScreenContent(
            modifier,
            isTimeUp,
            scrollState,
            onTimeToVote,
            timeRemaining,
            onRestartGameClicked,
            onEndGameClicked
        )

        if(shouldShowExitDialog) {
            EndGameDialog(
                onDismissRequest = { shouldShowExitDialog = false },
                onEndGame = onEndGameClicked
            )
        }
    }
}

@Composable
private fun GamePlayScreenContent(
    modifier: Modifier,
    isTimeUp: Boolean,
    scrollState: ScrollState,
    onTimeToVote: () -> Unit,
    timeRemaining: String,
    onRestartGameClicked: () -> Unit,
    onEndGameClicked: () -> Unit
) {

    LaunchedEffect(isTimeUp) {
        if (isTimeUp) {
            scrollState.animateScrollTo(0)
            onTimeToVote()
        }
    }

    Screen(
        modifier = modifier,
        topBar = {
            AdBanner(ad = OddOneOutAd.SingleDeviceGamePlayBanner)
        }
    ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = Dimension.D1000)
                    .verticalScroll(scrollState)
                ,
            ) {

                VerticalSpacerD1200()

                Text(
                    text = timeRemaining,
                    typography = OddOneOutTheme.typography.Display.D1400,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                VerticalSpacerD800()

                Text(
                    text = dictionaryString(R.string.singleDeviceGame_inGamePlayDescription_text),
                    typography = OddOneOutTheme.typography.Body.B800,
                )

                VerticalSpacerD800()

                Text(
                    text = dictionaryString(R.string.singleDeviceGame_inGamePlayExamples_header),
                    typography = OddOneOutTheme.typography.Body.B800.Bold,
                )

                VerticalSpacerD800()

                BulletRow {
                    Text(
                        text = dictionaryString(R.string.singleDeviceGame_inGamePlayExampleOne_text),
                        typography = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerD800()

                BulletRow {
                    Text(
                        text = dictionaryString(R.string.singleDeviceGamePlay_inGamePlayExampleTwo_text),
                        typography = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerD800()

                BulletRow {
                    Text(
                        text = dictionaryString(R.string.singleDeviceGamePlay_inGamePlayExampleThree_text),
                        typography = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerD800()

                BulletRow {
                    Text(
                        text = dictionaryString(R.string.singleDeviceGamePlay_inGamePlayExampleFour_text),
                        typography = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerD1200()

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRestartGameClicked
                ) {
                    Text(text = dictionaryString(R.string.singleDeviceGamePlay_inGamePlayRestart_action))
                }

                Spacer(modifier = Modifier.height(Dimension.D800))

                Button(
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEndGameClicked
                ) {
                    Text(text = dictionaryString(R.string.singleDeviceGamePlay_inGamePlayEndGame_action))
                }

                VerticalSpacerD1200()

            }
    }
}

@Composable
@Preview
private fun SingleDeviceGamePlayScreenPreview() {
    Preview {
        SingleDeviceGamePlayScreen(
            timeRemaining = "3:12",
            isTimeUp = false,
        )
    }
}