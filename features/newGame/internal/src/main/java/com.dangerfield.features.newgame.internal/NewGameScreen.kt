package com.dangerfield.features.newgame.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.components.Switch
import spyfallx.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    packs: List<DisplayPack>,
    onPackSelected: (DisplayPack, Boolean) -> Unit,
    name: String,
    onNameUpdated: (String) -> Unit,
    timeLimit: String,
    maxPlayers: Int,
    onTimeLimitUpdated: (String) -> Unit,
    isSingleDevice: Boolean,
    onIsSingleDeviceUpdated: (Boolean) -> Unit,
    numOfPlayers: String = "",
    onNumOfPlayersUpdated: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val (nameRequester) = FocusRequester.createRefs()

    Screen(
        modifier = modifier,
        header = {
            Header(title = "New Game")
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = Spacing.S1000)
        ) {
            Spacer(modifier = Modifier.height(Spacing.S1200))

            UserNameField(
                name = name,
                onNameUpdated = onNameUpdated,
                focusRequester = nameRequester
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            Text(text = "Packs:", typographyToken = SpyfallTheme.typography.Heading.H700)

            GamePackGrid(
                gamePacks = packs,
                onPackSelected = onPackSelected
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            GameLengthField(
                timeLimit = timeLimit,
                onTimeLimitUpdated = onTimeLimitUpdated
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            SingleDeviceField(
                isSingleDevice = isSingleDevice,
                onIsSingleDeviceUpdated = onIsSingleDeviceUpdated
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            NumOfPlayersField(
                numOfPlayers = numOfPlayers,
                onNumOfPlayersUpdated = onNumOfPlayersUpdated,
                maxPlayers = maxPlayers,
                isVisibile = isSingleDevice
            )

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

    LaunchedEffect(Unit) {
        nameRequester.requestFocus()
    }
}

@Composable
private fun NumOfPlayersField(
    numOfPlayers: String,
    onNumOfPlayersUpdated: (String) -> Unit,
    maxPlayers: Int,
    isVisibile: Boolean = false,
) {
    AnimatedVisibility(visible = isVisibile) {
        Row {
            Column {
                Text(
                    text = "Number of players",
                    typographyToken = SpyfallTheme.typography.Heading.H700
                )
                Text(
                    text = "How many roles will be in the game",
                    typographyToken = SpyfallTheme.typography.Body.B500
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                modifier = Modifier.width(IntrinsicSize.Max),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = numOfPlayers,
                onValueChange = { value ->
                    onNumOfPlayersUpdated(value)
                },
                placeholder = {
                    Text(text = "2-$maxPlayers")
                }
            )
        }
    }
}

@Composable
private fun SingleDeviceField(
    isSingleDevice: Boolean,
    onIsSingleDeviceUpdated: (Boolean) -> Unit
) {
    Row {
        Column {
            Text(
                text = "Play with single device",
                typographyToken = SpyfallTheme.typography.Heading.H700
            )
            Text(
                text = "Pass the device around to each player",
                typographyToken = SpyfallTheme.typography.Body.B500
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Switch(checked = isSingleDevice, onCheckedChange = onIsSingleDeviceUpdated)
    }
}

@Composable
private fun GameLengthField(
    timeLimit: String,
    onTimeLimitUpdated: (String) -> Unit,
) {
    Row {
        Column {
            Text(
                text = "Game Length:",
                typographyToken = SpyfallTheme.typography.Heading.H700
            )
            Text(
                text = "Pick a time length 10 mins or less",
                typographyToken = SpyfallTheme.typography.Body.B500
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedTextField(
            modifier = Modifier.width(IntrinsicSize.Max),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = timeLimit,
            onValueChange = { value ->
                onTimeLimitUpdated(value)
            },
            placeholder = {
                Text(text = "1-10")
            }
        )
    }
}

@Composable
private fun UserNameField(
    name: String,
    onNameUpdated: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Text(text = "User Name:", typographyToken = SpyfallTheme.typography.Heading.H700)
    Spacer(modifier = Modifier.height(Spacing.S600))

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        value = name,
        onValueChange = onNameUpdated,
        placeholder = {
            Text(text = "Pick a user name:")
        }
    )
}

@ThemePreviews
@Composable
fun PreviewNewGameScreen() {
    PreviewContent {
        var packs by remember {
            mutableStateOf(
                listOf(
                    DisplayPack(
                        type = "Standard",
                        number = "1",
                        key = "789",
                        isSelected = false
                    ),
                    DisplayPack(
                        type = "Standard",
                        number = "2",
                        key = "456",
                        isSelected = false
                    ),
                    DisplayPack(
                        type = "Special",
                        number = "1",
                        key = "123",
                        isSelected = false
                    ),
                )
            )
        }

        NewGameScreen(
            packs = packs,
            onPackSelected = { pack, isSelected ->
                packs = packs.map {
                    if(it.key == pack.key) {
                        it.copy(isSelected = isSelected)
                    } else {
                        it
                    }
                }
            },
            name = "Jeff",
            onNameUpdated = {},
            timeLimit = "5",
            onTimeLimitUpdated = {},
            isSingleDevice = true,
            onIsSingleDeviceUpdated = {},
            maxPlayers = 9
        )
    }
}
