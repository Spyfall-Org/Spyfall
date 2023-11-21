package com.dangerfield.features.joingame.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.ui.PreviewContent
import spyfallx.ui.Spacing
import spyfallx.ui.components.Screen
import spyfallx.ui.components.button.Button
import spyfallx.ui.components.text.Text
import spyfallx.ui.components.header.Header
import spyfallx.ui.components.text.OutlinedTextField
import spyfallx.ui.theme.SpyfallTheme

@Composable
fun JoinGameScreen() {
    Screen(
        header = {
            Header(title = "Join Game")
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = Spacing.S1000)) {
            Spacer(modifier = Modifier.height(Spacing.S1200))
            Text(text = "Access Code:", typographyToken = SpyfallTheme.typography.Heading.H700)
            Spacer(modifier = Modifier.height(Spacing.S600))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(text = "Enter the game access code:")
                }
            )

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

            Button(
                onClick = {  },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Join Game")
            }

        }
    }
}

@Preview
@Composable
fun PreviewJoinGameScreen() {
    PreviewContent() {
        JoinGameScreen(
        )
    }
}

@Preview
@Composable
fun PreviewJoinGameScreenDark() {
    PreviewContent(isDarkMode = true) {
        JoinGameScreen(
        )
    }
}

