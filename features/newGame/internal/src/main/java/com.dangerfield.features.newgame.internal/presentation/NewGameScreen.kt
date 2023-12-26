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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.features.newgame.internal.presentation.model.FieldState
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Switch
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.isOpen
import com.dangerfield.libraries.ui.rememberKeyboardState
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.icon.IconSize
import kotlinx.coroutines.launch

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
    isLoadingPacks: Boolean,
    isVideoCallLinkEnabled: Boolean,
    onIsSingleDeviceUpdated: (Boolean) -> Unit,
    numOfPlayersState: FieldState<String>,
    isSingleDeviceModeEnabled: Boolean,
    onNumOfPlayersUpdated: (String) -> Unit = {},
    packsState: FieldState<List<DisplayablePack>>,
    didSomethingGoWrong: Boolean = false,
    videoCallLinkState: FieldState<String>,
    onVideoCallLinkUpdated: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onVideoCallLinkInfoClicked: () -> Unit = {},
    onCreateGameClicked: () -> Unit = {},
    isLoadingCreation: Boolean,
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardState = rememberKeyboardState()
    var videoCallFieldHasFocus by remember { mutableStateOf(false) }
    var showPacksInfoBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(keyboardState, videoCallFieldHasFocus) {
        if (keyboardState.isOpen && videoCallFieldHasFocus) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box {
        Screen(
            modifier = modifier,
            topBar = {
                Header(
                    title = "New Game",
                    onNavigateBack = onNavigateBack,
                    liftOnScroll = true,
                    scrollState = scrollState
                )
            }
        ) { screenPadding ->
            Column(
                Modifier
                    .padding(screenPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = Spacing.S1000)
            ) {
                Spacer(modifier = Modifier.height(Spacing.S1200))

                if (isSingleDeviceModeEnabled) {
                    SingleDeviceField(
                        isSingleDevice = isSingleDevice,
                        onIsSingleDeviceUpdated = onIsSingleDeviceUpdated
                    )

                    VerticalSpacerS1200()
                }

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
                        if (!isSingleDevice) {
                            VerticalSpacerS1200()
                        }
                        PacksField(
                            isLoading = isLoadingPacks,
                            packsState = packsState,
                            onPackSelected = onPackSelected,
                            onPacksInfoClicked = {
                                showPacksInfoBottomSheet = true
                            },
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
                    isFieldVisible = isSingleDevice,
                    showErrorWhenNotFocused = true, // last field should show error,
                ) {
                    NumOfPlayersField(
                        numOfPlayersState = numOfPlayersState,
                        onNumOfPlayersUpdated = onNumOfPlayersUpdated,
                        minPlayers = minPlayers,
                        maxPlayers = maxPlayers
                    )
                }

                if (isVideoCallLinkEnabled) {
                    FormField(
                        formFieldState = videoCallLinkState,
                        isFieldVisible = !isSingleDevice,
                        showErrorWhenNotFocused = true,
                        onFocusChanged = { videoCallFieldHasFocus = it }

                    ) {
                        VideoCallLink(
                            onVideoCallLinkInfoClicked = onVideoCallLinkInfoClicked,
                            link = videoCallLinkState.backingValue.orEmpty(),
                            onLinkUpdated = onVideoCallLinkUpdated,
                        )
                    }
                }

                VerticalSpacerS1200()

                if (isLoadingCreation) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
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
                }

                VerticalSpacerS1200()
            }
        }

        if (didSomethingGoWrong) {
            NewGameErrorDialog(onDismissRequest = onNavigateBack)
        }

        if (showPacksInfoBottomSheet) {
            PacksInfoBottomSheet(
                packs = packsState.backingValue?.map { it.pack } ?: emptyList(),
                onDismiss = {
                    coroutineScope.launch {
                        it.hide()
                        showPacksInfoBottomSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun FormField(
    formFieldState: FieldState<*>,
    isFieldVisible: Boolean = true,
    showErrorWhenNotFocused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = isFieldVisible) {
        Column {
            Box(modifier = Modifier.onFocusChanged {
                hasFocus = it.hasFocus
                onFocusChanged(it.hasFocus)
            }) {
                content()
            }

            VerticalSpacerS500()

            if (formFieldState is FieldState.Invalid && (!hasFocus || showErrorWhenNotFocused)) {
                Text(
                    text = formFieldState.errorMessage,
                    typographyToken = OddOneOutTheme.typography.Body.B500,
                    color = OddOneOutTheme.colorScheme.textWarning
                )
            }
        }
    }
}

@Composable
private fun PacksField(
    packsState: FieldState<List<DisplayablePack>>,
    onPackSelected: (DisplayablePack, Boolean) -> Unit,
    onPacksInfoClicked: () -> Unit = {},
    isLoading: Boolean
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier) {

        Row {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                AsteriskText {
                    Text(text = "Packs:")
                }
                Text(
                    text = "Choose which packs you want to play with",
                    typographyToken = OddOneOutTheme.typography.Body.B500
                )
            }

            HorizontalSpacerS600()

            IconButton(
                icon = SpyfallIcon.Info("Packs Information"),
                onClick = onPacksInfoClicked
            )
        }
        
        VerticalSpacerS500()

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            GamePackGrid(
                gamePacks = packsState.backingValue ?: emptyList(),
                onPackSelected = { pack, isSelected ->
                    focusManager.clearFocus()
                    onPackSelected(pack, isSelected)
                }
            )
        }
    }
}

@Composable
private fun NumOfPlayersField(
    onNumOfPlayersUpdated: (String) -> Unit,
    minPlayers: Int,
    maxPlayers: Int,
    numOfPlayersState: FieldState<String>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
                typographyToken = OddOneOutTheme.typography.Body.B500
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
                typographyToken = OddOneOutTheme.typography.Body.B500,
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
    onVideoCallLinkInfoClicked: () -> Unit,
) {
    Column {
        Row {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Video Call Link:",
                )
                Text(
                    text = "Past a video call link if you have one:",
                    typographyToken = OddOneOutTheme.typography.Body.B500
                )
            }

            HorizontalSpacerS600()

            IconButton(
                icon = SpyfallIcon.Info("Video Call Link Information"),
                onClick = onVideoCallLinkInfoClicked
            )
        }

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
            minPlayers = 3,
            maxPlayers = 9,
            minGameLength = 1,
            maxGameLength = 10,
            isFormValid = false,
            isVideoCallLinkEnabled = true,
            isLoadingPacks = false,
            onIsSingleDeviceUpdated = { isSingleDevice = it },
            numOfPlayersState = FieldState.Valid(""),
            isSingleDeviceModeEnabled = true,
            onNumOfPlayersUpdated = { },
            packsState = FieldState.Valid(packs),
            didSomethingGoWrong = false,
            videoCallLinkState = FieldState.Valid(""),
            onVideoCallLinkUpdated = {},
            onNavigateBack = { },
            onCreateGameClicked = { },
            isLoadingCreation = false,
            onVideoCallLinkInfoClicked = { -> },
        )
    }
}
