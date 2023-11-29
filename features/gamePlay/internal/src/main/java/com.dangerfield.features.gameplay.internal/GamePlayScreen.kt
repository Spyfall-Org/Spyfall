package com.dangerfield.features.gameplay.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
fun GamePlayScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val players = listOf("Adam", "Ryan", "Jim", "Pam", "Michael", "Justin", "Bryan","Adam", "Ryan", "Jim", "Pam", "Michael")
    val locations = listOf("Library", "Space", "Cafe", "Gas Station", "Gym", "School", "Office", "Gas Station", "Gym", "School", "Office")

    Screen(modifier = modifier.verticalScroll(scrollState)) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(top = Spacing.S1200)
                .padding(horizontal = Spacing.S1000),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "1:32",
                typographyToken = SpyfallTheme.typography.Display.D1100
            )

            RoleCard(role = "The Spy", text = " Don't get found out")

            Spacer(modifier = Modifier.height(Spacing.S1000))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Players:",
                typographyToken = SpyfallTheme.typography.Display.D800
            )

            GamePlayGrid(items = players)

            Spacer(modifier = Modifier.height(Spacing.S1000))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Locations:",
                typographyToken = SpyfallTheme.typography.Display.D800
            )

            GamePlayGrid(items = locations)

            Spacer(modifier = Modifier.height(Spacing.S1000))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Leave Game")
            }

            Spacer(modifier = Modifier.height(Spacing.S800))

            Button(
                type = ButtonType.Regular,
                modifier = Modifier.fillMaxWidth(),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "End Game")
            }


        }
    }
}


@Composable
@Preview
private fun PreviewGamePlayScreen() {
    PreviewContent {
        GamePlayScreen()
    }
}
