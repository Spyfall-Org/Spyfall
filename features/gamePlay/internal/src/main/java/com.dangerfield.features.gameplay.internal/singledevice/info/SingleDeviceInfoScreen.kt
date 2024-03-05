package com.dangerfield.features.gameplay.internal.singledevice.info

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dangerfield.features.gameplay.internal.singledevice.EndGameDialog
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.VerticalSpacerD1000
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.gameplay.internal.R

@Composable
fun SingleDeviceInfoScreen(
    onStartClicked: () -> Unit,
    onEndGameClicked: () -> Unit
) {

    var showBackDialog by remember { mutableStateOf(false) }

    BackHandler {
        showBackDialog = !showBackDialog
    }

    Box {
        SingleDeviceInfoScreenContent(
            onStartClicked = onStartClicked,
            onEndGameClicked = onEndGameClicked
        )

        if (showBackDialog) {
            EndGameDialog(
                onDismissRequest = { showBackDialog = false },
                onEndGame = onEndGameClicked
            )
        }
    }
}

@Composable
private fun SingleDeviceInfoScreenContent(
    onStartClicked: () -> Unit,
    onEndGameClicked: () -> Unit
) {
    Screen(
        topBar = {
            Header(
                title = dictionaryString(R.string.singleDeviceInfo_gettingStarted_header),
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = Dimension.D1000)
        ) {
            VerticalSpacerD500()

            Text(
                text = dictionaryString(R.string.singleDeviceInfo_gameInfo_text),
                typography = OddOneOutTheme.typography.Body.B700
            )

            VerticalSpacerD1200()

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onStartClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(R.string.singleDeviceInfo_startGame_action))
            }

            VerticalSpacerD1000()

            Button(
                type = ButtonType.Secondary,
                onClick = onEndGameClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(R.string.singleDeviceInfo_endGame_action))
            }

            VerticalSpacerD1000()
        }
    }
}

@Composable
@Preview
fun SingleDeviceInfoScreenPreview() {
    Preview {
        SingleDeviceInfoScreen(
            onStartClicked = {},
            onEndGameClicked = {}
        )
    }
}