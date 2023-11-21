package com.dangerfield.features.newgame.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.ui.PreviewContent
import spyfallx.ui.Spacing
import spyfallx.ui.color.ColorPrimitive
import spyfallx.ui.components.Screen
import spyfallx.ui.components.button.Button
import spyfallx.ui.components.header.Header
import spyfallx.ui.components.text.OutlinedTextField
import spyfallx.ui.components.text.Text
import spyfallx.ui.theme.SpyfallTheme

@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Screen(
        modifier = modifier,
        header = {
            Header(title = "New Game")
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = Spacing.S1000)
                .verticalScroll(scrollState),

            ) {
            Spacer(modifier = Modifier.height(Spacing.S1200))

            Text(text = "User Name:", typographyToken = SpyfallTheme.typography.Heading.H700)
            Spacer(modifier = Modifier.height(Spacing.S600))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(text = "Pick a user name:")
                }
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            Text(text = "Packs:", typographyToken = SpyfallTheme.typography.Heading.H700)

            //TODO gamepacks should be in a config of some sort
            // and should be served by some repository
            GamePackGrid(gamePacks = listOf(
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "2", type = "Standard Pack"),
                GamePack(number = "1", type = "Special Pack"),
                GamePack(number = "1", type = "Standard Pack"),
            ))

            Spacer(modifier = Modifier.height(Spacing.S1200))

            Row {
                Column {
                    Text(text = "Game Length:", typographyToken = SpyfallTheme.typography.Heading.H700)
                    Text(text = "Pick a time length 10 mins or less", typographyToken = SpyfallTheme.typography.Body.B500)
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedTextField(
                    value = "",
                    modifier = Modifier.width(IntrinsicSize.Max),
                    onValueChange = {},
                    placeholder = {
                        Text(text = "1-10")
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.S1200))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Game")
            }

            Spacer(modifier = Modifier.height(Spacing.S1200))
        }
    }
}

@Preview
@Composable
fun PreviewNewGameScreen() {
    PreviewContent() {
        NewGameScreen()
    }
}

@Preview
@Composable
fun PreviewNewGameScreenDark() {
    PreviewContent(isDarkMode = true, accentColor = ColorPrimitive.MintyFresh300) {
        NewGameScreen()
    }
}
