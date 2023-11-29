package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.features.newgame.internal.presentation.model.FieldState
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.HorizontalSpacerS200
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Switch
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.core.toStringOrEmpty
import spyfallx.ui.Spacing

@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    onPackSelected: (DisplayablePack, Boolean) -> Unit,
    nameState: FieldState<String>,
    onNameUpdated: (String) -> Unit,
    timeLimitState: FieldState<String>,
    onTimeLimitUpdated: (String) -> Unit,
    isSingleDevice: Boolean,
    minPlayers: Int,
    maxPlayers: Int,
    minGameLength: Int,
    maxGameLength: Int,
    isFormValid: Boolean,
    onIsSingleDeviceUpdated: (Boolean) -> Unit,
    numOfPlayersState: FieldState<String>,
    onNumOfPlayersUpdated: (String) -> Unit = {},
    packsState: FieldState<List<DisplayablePack>>,
    didSomethingGoWrong: Boolean = false,
    videoCallLink: String,
    onVideoCallLinkUpdated: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onCreateGameClicked: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box {
        Screen(
            modifier = modifier,
            header = {
                Header(
                    title = "New Game",
                    onNavigateBack = onNavigateBack,
                    liftOnScroll = true,
                    scrollState = scrollState
                )
            }
        ) {
            Column(
                Modifier
                    .verticalScroll(scrollState)
                    .padding(it)
                    .padding(horizontal = Spacing.S1000)
            ) {
                Spacer(modifier = Modifier.height(Spacing.S1200))

                SingleDeviceField(
                    isSingleDevice = isSingleDevice,
                    onIsSingleDeviceUpdated = onIsSingleDeviceUpdated
                )

                VerticalSpacerS1200()

                FormField(
                    formFieldState = nameState,
                    isFieldVisible = !isSingleDevice
                ) {
                    UserNameField(
                        nameState = nameState,
                        onNameUpdated = onNameUpdated,
                    )
                }

                FormField(formFieldState = packsState) {
                    Column {
                        if(!isSingleDevice) {
                            VerticalSpacerS1200()
                        }
                        PacksField(
                            packsState = packsState,
                            onPackSelected = onPackSelected
                        )
                    }
                }

                VerticalSpacerS1200()

                FormField(formFieldState = timeLimitState) {
                    GameLengthField(
                        timeLimitState = timeLimitState,
                        onTimeLimitUpdated = onTimeLimitUpdated,
                        minGameLength = minGameLength,
                        maxGameLength = maxGameLength
                    )
                }

                VerticalSpacerS1200()

                FormField(
                    formFieldState = numOfPlayersState,
                    isFieldVisible = isSingleDevice
                ) {
                    NumOfPlayersField(
                        numOfPlayersState = numOfPlayersState,
                        onNumOfPlayersUpdated = onNumOfPlayersUpdated,
                        minPlayers = minPlayers,
                        maxPlayers = maxPlayers
                    )
                }

                FormField(
                    formFieldState = FieldState.Valid(videoCallLink),
                    isFieldVisible = !isSingleDevice
                ) {
                    VideoCallLink(
                        link = videoCallLink,
                        onLinkUpdated = onVideoCallLinkUpdated,
                    )
                }

                VerticalSpacerS1200()

                Button(
                    style = if (isFormValid) ButtonStyle.Filled else ButtonStyle.Outlined,
                    enabled = isFormValid,
                    onClick = {
                        focusManager.clearFocus()
                        onCreateGameClicked()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Create Game")
                }

                VerticalSpacerS1200()
            }
        }

        if (didSomethingGoWrong) {
            NewGameErrorDialog(onDismiss = onNavigateBack)
        }
    }
}

@Composable
private fun FormField(
    formFieldState: FieldState<*>,
    isFieldVisible: Boolean = true,
    content: @Composable () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = isFieldVisible) {
        Column {
            Box(modifier = Modifier.onFocusChanged { hasFocus = it.hasFocus }) {
                content()
            }

            if (formFieldState is FieldState.Invalid && !hasFocus) {
                Text(
                    text = formFieldState.errorMessage,
                    typographyToken = SpyfallTheme.typography.Body.B500,
                    color = SpyfallTheme.colorScheme.textWarning
                )
            }
        }
    }
}

@Composable
private fun PacksField(
    packsState: FieldState<List<DisplayablePack>>,
    onPackSelected: (DisplayablePack, Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier) {
        AsteriskText {
            Text(text = "Packs:")
        }

        GamePackGrid(
            gamePacks = packsState.backingValue ?: emptyList(),
            onPackSelected = { pack, isSelected ->
                focusManager.clearFocus()
                onPackSelected(pack, isSelected)
            }
        )
    }
}

@Composable
private fun NumOfPlayersField(
    onNumOfPlayersUpdated: (String) -> Unit,
    minPlayers: Int,
    maxPlayers: Int,
    numOfPlayersState: FieldState<String>,
) {
    Column {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                AsteriskText {
                    Text(
                        text = "Number of players",
                    )
                }
            }

            HorizontalSpacerS600()

            OutlinedTextField(
                modifier = Modifier.width(IntrinsicSize.Max),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = numOfPlayersState.backingValue.orEmpty(),
                onValueChange = onNumOfPlayersUpdated,
                placeholder = {
                    Text(text = "$minPlayers-$maxPlayers")
                },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(Spacing.S1200))
    }

}

@Composable
private fun SingleDeviceField(
    isSingleDevice: Boolean,
    onIsSingleDeviceUpdated: (Boolean) -> Unit
) {
    Row {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Single Device Mode",
            )
            Text(
                text = "Pass the device around to each player",
                typographyToken = SpyfallTheme.typography.Body.B500
            )
        }

        HorizontalSpacerS600()

        Switch(checked = isSingleDevice, onCheckedChange = onIsSingleDeviceUpdated)
    }
}

@Composable
private fun GameLengthField(
    timeLimitState: FieldState<String>,
    onTimeLimitUpdated: (String) -> Unit,
    minGameLength: Int,
    maxGameLength: Int
) {
    Row {
        Column(Modifier.weight(1f)) {
            AsteriskText {
                Text(
                    text = "Game Length:",
                )
            }
            Text(
                text = "How many minutes should each round last:",
                typographyToken = SpyfallTheme.typography.Body.B500,
            )
        }

        HorizontalSpacerS600()

        OutlinedTextField(
            modifier = Modifier.width(IntrinsicSize.Max),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = timeLimitState.backingValue.orEmpty(),
            onValueChange = onTimeLimitUpdated,
            placeholder = {
                Text(text = "$minGameLength-$maxGameLength")
            },
            singleLine = true
        )
    }
}

@Composable
private fun UserNameField(
    nameState: FieldState<String>,
    onNameUpdated: (String) -> Unit,
) {
    Column {
        AsteriskText {
            Text(text = "User Name:")
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = nameState.backingValue.orEmpty(),
            onValueChange = onNameUpdated,
            placeholder = {
                Text(text = "Pick a user name:")
            },
            singleLine = true
        )
    }

}

@Composable
private fun VideoCallLink(
    link: String,
    onLinkUpdated: (String) -> Unit,
) {
    Column {
        Text(
            text = "Video Call Link:",
        )
        Text(
            text = "Past a video call link if you have one:",
            typographyToken = SpyfallTheme.typography.Body.B500
        )

        Spacer(modifier = Modifier.height(Spacing.S600))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = link,
            onValueChange = onLinkUpdated,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
            singleLine = true
        )
    }
}

@ThemePreviews
@Composable
fun PreviewNewGameScreen() {
    PreviewContent {
        var isSingleDevice by remember { mutableStateOf(false) }
        var packs by remember {
            mutableStateOf(
                listOf(
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 1", locations = listOf())
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 2", locations = listOf())
                    ),

                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 3", locations = listOf())
                    ),

                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 4", locations = listOf())
                    ),
                )
            )
        }

        NewGameScreen(
            onPackSelected = { displayablePack, isSelected ->
                packs = packs.map {
                    if (it.pack == displayablePack.pack) {
                        it.copy(isSelected = isSelected)
                    } else {
                        it
                    }
                }
            },
            nameState = FieldState.Valid(""),
            onNameUpdated = {},
            timeLimitState = FieldState.Valid(""),
            onTimeLimitUpdated = {},
            isSingleDevice = isSingleDevice,
            onIsSingleDeviceUpdated = { isSingleDevice = it },
            minPlayers = 3,
            maxPlayers = 9,
            videoCallLink = "",
            onVideoCallLinkUpdated = {},
            maxGameLength = 10,
            minGameLength = 1,
            numOfPlayersState = FieldState.Valid(""),
            onNumOfPlayersUpdated = { },
            didSomethingGoWrong = false,
            onNavigateBack = { },
            onCreateGameClicked = { },
            packsState = FieldState.Valid(packs),
            isFormValid = false
        )
    }
}
