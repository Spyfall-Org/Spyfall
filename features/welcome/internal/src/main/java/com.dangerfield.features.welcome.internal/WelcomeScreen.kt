package com.dangerfield.features.welcome.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.SlideFrom
import com.dangerfield.libraries.ui.SlideInContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.IconButton.Size.Medium
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.PreviewContent
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
    Screen(
        topBar = {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    icon = SpyfallIcon.Settings(""),
                    onClick = onSettingsClicked,
                    size = Medium,
                    modifier = Modifier.padding(Spacing.S800),
                )
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = Spacing.S1100),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            VerticalSpacerS800()

            WelcomeText()

            Spacer(modifier = Modifier.weight(1f))

            Options(
                onNewGameClicked = onNewGameClicked,
                onJoinGameClicked = onJoinGameClicked,
                onRulesClicked = onRulesClicked
            )

            Spacer(modifier = Modifier.height(Spacing.S1000))
        }
    }
}

@Composable
private fun Options(
    onNewGameClicked: () -> Unit,
    onJoinGameClicked: () -> Unit,
    onRulesClicked: () -> Unit
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = onNewGameClicked,
            modifier = Modifier.fillMaxWidth(),
            type = ButtonType.Primary
        ) {
            Text(text = dictionaryString(R.string.welcome_newGame_action))
        }


        Spacer(modifier = Modifier.height(Spacing.S1000))

        Button(
            type = ButtonType.Secondary,
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
}

@Composable
private fun WelcomeText() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = dictionaryString(R.string.welcome_intro_header),
            typographyToken = OddOneOutTheme.typography.Display.D1000,
            textAlign = TextAlign.Start
        )

        VerticalSpacerS500()

        Image(
            painter = painterResource(id = R.drawable.ooo_phrase_logo),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
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
            onRulesClicked = {},
        )
    }
}
