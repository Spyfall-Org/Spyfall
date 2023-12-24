package com.dangerfield.features.gameplay.internal.singledevice

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.ui.RoleCard
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SingleDevicePlayerRoleScreen(
    currentPlayer: DisplayablePlayer?,
    location: String?,
    onNextPlayerClicked: () -> Unit,
    isLastPlayer: Boolean,
    nameFieldState: FieldState<String?>,
    onStartGameClicked: () -> Unit,
    onNameUpdated: (String) -> Unit
) {
    var isRoleHidden by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    ScrollingColumnWithFadingEdge(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.S1100)
            .padding(bottom = Spacing.S1000),
        horizontalAlignment = CenterHorizontally
    ) {

        // TODO add horizontal pager
        VerticalSpacerS1200()

        if (currentPlayer != null) {

            Text(
                text = "Hand the device to ${currentPlayer.name}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                typographyToken = OddOneOutTheme.typography.Display.D1100
            )

            VerticalSpacerS800()

            if (isLastPlayer) {
                Text(
                    text = "Click \"show\" to see your role.\n\n When everyone is ready, click start game to begin.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typographyToken = OddOneOutTheme.typography.Body.B700
                )
            } else {
                Text(
                    text = "Click \"show\" to see your role.\n\n When you are ready, click next player and pass the device to the next player.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typographyToken = OddOneOutTheme.typography.Body.B700
                )
            }

            VerticalSpacerS1200()

            RoleCard(
                role = currentPlayer.role,
                isTheOddOneOut = currentPlayer.isOddOneOut,
                location = location,
                text = null,
                isHidden = isRoleHidden,
                onHideShowClicked = { isRoleHidden = !isRoleHidden }
            )

            VerticalSpacerS1200()

            Text(
                text = "Remember which player you are or change your name.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            VerticalSpacerS500()

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = nameFieldState.backingValue.orEmpty(),
                onValueChange = onNameUpdated,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                placeholder = {
                    Text(text = "Enter new name")
                },
                singleLine = true
            )

            if (nameFieldState is FieldState.Invalid) {
                VerticalSpacerS500()
                Text(
                    text = nameFieldState.errorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    typographyToken = OddOneOutTheme.typography.Body.B500,
                    color = OddOneOutTheme.colorScheme.textWarning
                )
            }

            if (nameFieldState is FieldState.Valid) {
                VerticalSpacerS500()
                Text(
                    text = "Name will be updated",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    typographyToken = OddOneOutTheme.typography.Body.B500,
                    colorPrimitive = ColorPrimitive.MintyFresh300
                )
            }

            VerticalSpacerS1200()

            val areButtonsEnabled =
                nameFieldState is FieldState.Valid || nameFieldState is FieldState.Idle

            if (isLastPlayer) {
                Button(
                    onClick = onStartGameClicked,
                    modifier = Modifier.fillMaxWidth(),
                    style = if (areButtonsEnabled) ButtonStyle.Filled else ButtonStyle.Outlined,
                    enabled = areButtonsEnabled,
                ) {
                    Text(text = "Start Game")
                }
            } else {
                Button(
                    onClick = {
                        isRoleHidden = true
                        coroutineScope.launch {
                            delay(200)
                            onNextPlayerClicked()
                        }
                    },
                    style = if (areButtonsEnabled) ButtonStyle.Filled else ButtonStyle.Outlined,
                    enabled = areButtonsEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Next Player")
                }
            }
        } else {
            Text(
                text = "Loading...",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                typographyToken = OddOneOutTheme.typography.Display.D1100
            )
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewSingleDevicePlayerRoleScreen() {
    PreviewContent {
        SingleDevicePlayerRoleScreen(
            currentPlayer = DisplayablePlayer(
                name = "Jane",
                id = "",
                role = "The Odd One Out",
                isFirst = false,
                isOddOneOut = true
            ),
            location = "Something",
            onNextPlayerClicked = { -> },
            isLastPlayer = false,
            onStartGameClicked = { -> },
            nameFieldState = FieldState.Valid("Jane"),
            onNameUpdated = { },
        )
    }
}