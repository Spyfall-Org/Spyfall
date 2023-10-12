package com.dangerfield.features.joingame.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Spacing
import spyfallx.coreui.components.Screen
import spyfallx.coreui.components.button.Button
import spyfallx.coreui.components.text.Text
import spyfallx.coreui.components.header.Header
import spyfallx.coreui.components.text.OutlinedTextField
import spyfallx.coreui.icon.SpyfallIcon
import spyfallx.coreui.theme.SpyfallTheme

@Composable
fun JoinGameScreen() {
    Screen(
        header = {
            Header(title = "Join Game", navigationIcon = SpyfallIcon.ArrowBack)
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
    PreviewContent {
        JoinGameScreen()
    }
}