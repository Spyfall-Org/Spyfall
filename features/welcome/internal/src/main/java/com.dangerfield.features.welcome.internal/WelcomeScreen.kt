package com.dangerfield.features.welcome.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.IconButton.Size.Medium
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.welcome.internal.R

@Composable
fun WelcomeScreen(
    onNewGameClicked: () -> Unit,
    onJoinGameClicked: () -> Unit,
    onRulesClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {

    WelcomeScreenContent(
        onNewGameClicked = onNewGameClicked,
        onJoinGameClicked = onJoinGameClicked,
        onSettingsClicked = onSettingsClicked,
        onRulesClicked = onRulesClicked,
    )
}

@Composable
private fun WelcomeScreenContent(
    onNewGameClicked: () -> Unit,
    onJoinGameClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onRulesClicked: () -> Unit,
    ) {
    Screen { paddingValues ->
        Column(
            Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)) {
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    icon = SpyfallIcon.Settings(""),
                    onClick = onSettingsClicked,
                    size = Medium,
                    modifier = Modifier.padding(Spacing.S800),
                )
            }
            Text(
                text = dictionaryString(R.string.welcome_intro_header),
                typographyToken = OddOneOutTheme.typography.Display.D1200.Bold,
                modifier = Modifier.padding(horizontal = Spacing.S500),
                textAlign = TextAlign.Center
            )

            Text(
                text = dictionaryString(R.string.app_name),
                typographyToken = OddOneOutTheme.typography.Display.D1200.Bold,
                modifier = Modifier.padding(horizontal = Spacing.S500),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = Spacing.S1100),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = onNewGameClicked,
                    modifier = Modifier.fillMaxWidth(),
                    type = ButtonType.Accent
                ) {
                    Text(text = dictionaryString(R.string.welcome_newGame_action))
                }

                Spacer(modifier = Modifier.height(Spacing.S1000))

                Button(
                    type = ButtonType.Regular,
                    onClick = onJoinGameClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = dictionaryString(R.string.welcome_join_action))
                }

                Spacer(modifier = Modifier.height(Spacing.S1000))

                Button(
                    icon = SpyfallIcon.Info(null),
                    onClick = onRulesClicked,
                    style = ButtonStyle.NoBackground
                ) {
                    Text(text = dictionaryString(R.string.welcome_rules_action))
                }
            }

            Spacer(modifier = Modifier.height(Spacing.S1000))
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewWelcomeScreen() {
    PreviewContent {
        WelcomeScreenContent(
            onNewGameClicked = {},
            onJoinGameClicked = {},
            onSettingsClicked = {},
            onRulesClicked = {},
        )
    }
}
