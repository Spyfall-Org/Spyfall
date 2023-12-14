package com.dangerfield.features.welcome.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dangerfield.features.welcome.internal.WelcomeViewModel.WelcomeEvent.ForcedUpdateRequired
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.IconButton.Size.Medium
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.spyfall.libraries.resources.R
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
@Suppress("MagicNumber")
fun WelcomeScreen(
    onForcedUpdateRequired: () -> Unit = {},
    onNewGameClicked: () -> Unit,
    onJoinGameClicked: () -> Unit,
    onRulesClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    when {
        state.value.events.contains(ForcedUpdateRequired) -> {
            viewModel.onEventHandled(ForcedUpdateRequired)
            onForcedUpdateRequired()
        }
    }

    WelcomeScreenContent(
        onNewGameClicked = onNewGameClicked,
        onJoinGameClicked = onJoinGameClicked,
        onSettingsClicked = onSettingsClicked,
        onRulesClicked = onRulesClicked
    )
}

@Composable
@Suppress("MagicNumber")
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
                    icon = SpyfallIcon.Settings("settings"),
                    onClick = onSettingsClicked,
                    size = Medium,
                    modifier = Modifier.padding(Spacing.S800),
                )
            }
            Text(
                text = "Welcome to",
                typographyToken = OddOneOutTheme.typography.Display.D1200.Bold,
                modifier = Modifier.padding(horizontal = Spacing.S500),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.app_name),
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
                    type = ButtonType.Regular
                ) {
                    Text(text = "New Game")
                }

                Spacer(modifier = Modifier.height(Spacing.S1000))

                Button(
                    onClick = onJoinGameClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Join Game")
                }

                Spacer(modifier = Modifier.height(Spacing.S1000))

                Button(
                    icon = SpyfallIcon.Info("Open Rules"),
                    onClick = onRulesClicked,
                    style = ButtonStyle.NoBackground
                ) {
                    Text(text = "Rules")
                }
            }
            Spacer(modifier = Modifier.height(Spacing.S1000))
        }
    }
}


@Composable
@Preview
private fun PreviewWelcomeScreen() {
    PreviewContent {
        WelcomeScreenContent(
            onNewGameClicked = {},
            onJoinGameClicked = {},
            onSettingsClicked = {},
            onRulesClicked = {}
        )
    }
}

@Composable
@Preview
private fun PreviewWelcomeScreenDark() {
    PreviewContent(isDarkMode = true) {
        WelcomeScreenContent(
            onNewGameClicked = {},
            onJoinGameClicked = {},
            onSettingsClicked = {},
            onRulesClicked = {}
        )
    }
}
