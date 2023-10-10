package com.dangerfield.features.welcome.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.components.Screen
import spyfallx.coreui.components.Text

@Composable
fun WelcomeScreen() {
    Screen { paddingValues ->
        Column(Modifier.padding(paddingValues), verticalArrangement = Arrangement.Center) {
            Text(text = "Welcome to Spyfall")
        }
    }
}


@Composable
@Preview
private fun PreviewWelcomeScreen() {
    PreviewContent {
        WelcomeScreen()
    }
}