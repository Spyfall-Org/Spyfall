package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun NewGameOfflineScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {

    Screen(
        modifier = modifier,
        topBar = {
            Header(
                title = dictionaryString(R.string.newGame_newGameScreen_header),
                onNavigateBack = onNavigateBack,
            )
        }
    ) { screenPadding ->
        Column(
            Modifier
                .padding(screenPadding)
                .padding(horizontal = Dimension.D1000)
        ) {
            VerticalSpacerD1200()

            Text(text = dictionaryString(R.string.newGame_offlineScreen_header))

            VerticalSpacerD500()

            Text(
                text = dictionaryString(R.string.newGame_offlineNoSupport_text),
                typographyToken = OddOneOutTheme.typography.Body.B700
            )

        }
    }
}


@Preview
@Composable
private fun PreviewNewGameOfflineScreen() {
    Preview {
        NewGameOfflineScreen(
            onNavigateBack = { }
        )
    }
}