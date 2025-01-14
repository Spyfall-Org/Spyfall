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
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.newgame.internal.presentation.model.NewGamePackOption
import com.dangerfield.features.newgame.newGameNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.HorizontalSpacerD600
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Switch
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.isOpen
import com.dangerfield.libraries.ui.rememberKeyboardState
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R
import kotlinx.coroutines.launch

@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    onPackSelected: (NewGamePackOption, Boolean) -> Unit,
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
    onCreateYourOwnPackClicked: () -> Unit,
    onIsSingleDeviceUpdated: (Boolean) -> Unit,
    numOfPlayersState: FieldState<String>,
    isSingleDeviceModeEnabled: Boolean,
    isCreateYourOwnNew: Boolean,
    onNumOfPlayersUpdated: (String) -> Unit,
    packsState: FieldState<List<NewGamePackOption>>,
    didCreationFail: Boolean = false,
    didLoadFail: Boolean = false,
    onErrorDismissed: () -> Unit,
    videoCallLinkState: FieldState<String>,
    onVideoCallLinkUpdated: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onVideoCallLinkInfoClicked: () -> Unit,
    onCreateGameClicked: () -> Unit,
    isLoadingCreation: Boolean,
    isOffline: Boolean,
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardState = rememberKeyboardState()
    var videoCallFieldHasFocus by remember { mutableStateOf(false) }
    var showPacksInfoBottomSheet by remember { mutableStateOf(false) }

    PageLogEffect(
        route = newGameNavigationRoute,
        type = PageType.FullScreenPage
    )

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
                    title = dictionaryString(R.string.newGame_newGameScreen_header),
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
                    .padding(horizontal = Dimension.D1000)
            ) {
                Spacer(modifier = Modifier.height(Dimension.D500))

                if (isSingleDeviceModeEnabled) {
                    if (isOffline) {
                        OfflineGameBanner()
                    } else {
                        SingleDeviceField(
                            isSingleDevice = isSingleDevice,
                            onIsSingleDeviceUpdated = onIsSingleDeviceUpdated,
                        )
                    }

                    VerticalSpacerD1200()
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
                            VerticalSpacerD1200()
                        }
                        PacksField(
                            isLoading = isLoadingPacks,
                            packsState = packsState,
                            onPackSelected = onPackSelected,
                            isCreateYourOwnNew = isCreateYourOwnNew,
                            onCreateYourOwnPackClicked = onCreateYourOwnPackClicked,
                            onPacksInfoClicked = {
                                showPacksInfoBottomSheet = true
                            },
                        )
                    }
                }

                VerticalSpacerD1200()

                FormField(formFieldState = timeLimitState) {
                    GameLengthField(
                        timeLimitState = timeLimitState,
                        onTimeLimitUpdated = onTimeLimitUpdated,
                        minGameLength = minGameLength,
                        maxGameLength = maxGameLength
                    )
                }

                VerticalSpacerD1200()

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
                            link = videoCallLinkState.value.orEmpty(),
                            onLinkUpdated = onVideoCallLinkUpdated,
                        )
                    }
                }

                VerticalSpacerD1200()

                if (isLoadingCreation) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Button(
                        style = ButtonStyle.Background,
                        enabled = isFormValid,
                        onClick = {
                            focusManager.clearFocus()
                            onCreateGameClicked()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = dictionaryString(R.string.newGame_createGame_action))
                    }
                }

                VerticalSpacerD1200()
            }
        }

        if (didCreationFail) {
            CantCreateGameErrorDialog(onDismissRequest = onErrorDismissed)
        }

        if (didLoadFail) {
            LoadGameOptionsErrorDialog(onDismissRequest = onErrorDismissed)
        }

        if (showPacksInfoBottomSheet) {
            PacksInfoBottomSheet(
                packs = packsState.value ?: emptyList(),
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

            VerticalSpacerD500()

            if (formFieldState is FieldState.Invalid && (!hasFocus || showErrorWhenNotFocused)) {
                Text(
                    text = formFieldState.errorMessage,
                    typography = OddOneOutTheme.typography.Body.B500,
                    colorResource = OddOneOutTheme.colors.textWarning
                )
            }
        }
    }
}

@Composable
private fun PacksField(
    packsState: FieldState<List<NewGamePackOption>>,
    onPackSelected: (NewGamePackOption, Boolean) -> Unit,
    onCreateYourOwnPackClicked: () -> Unit,
    onPacksInfoClicked: () -> Unit = {},
    isCreateYourOwnNew: Boolean,
    isLoading: Boolean
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier) {

        Row {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                AsteriskText {
                    Text(text = dictionaryString(R.string.newGame_packs_header))
                }
                Text(
                    text = dictionaryString(R.string.newGame_packsDescription_text),
                    typography = OddOneOutTheme.typography.Body.B500
                )
            }

            HorizontalSpacerD600()

            IconButton(
                icon = SpyfallIcon.Info(dictionaryString(R.string.newGame_packsInfo_a11y)),
                onClick = onPacksInfoClicked
            )
        }

        VerticalSpacerD500()

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            GamePackGrid(
                gamePacks = packsState.value.orEmpty(),
                onPackSelected = { pack, isSelected ->
                    focusManager.clearFocus()
                    onPackSelected(pack, isSelected)
                },
                isCreateYourOwnNew = isCreateYourOwnNew,
                onCreateYourOwnSelected = onCreateYourOwnPackClicked
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
        Column(Modifier.weight(1f)) {
            Text(
                text = dictionaryString(R.string.newGame_singleDevice_label),
            )
            Text(
                text = dictionaryString(R.string.newGame_singleDeviceDescription_text),
                typography = OddOneOutTheme.typography.Body.B500
            )
        }

        HorizontalSpacerD600()

        Switch(
            checked = isSingleDevice,
            onCheckedChange = onIsSingleDeviceUpdated,
        )
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
                    text = dictionaryString(R.string.newGame_gameLength_header),
                )
            }
            Text(
                text = dictionaryString(R.string.newGame_gameLengthDescription_text),
                typography = OddOneOutTheme.typography.Body.B500,
            )
        }

        HorizontalSpacerD600()

        OutlinedTextField(
            modifier = Modifier.width(IntrinsicSize.Max),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = timeLimitState.value.orEmpty(),
            onValueChange = onTimeLimitUpdated,
            placeholder = {
                Text(text = "$minGameLength-$maxGameLength")
            },
            singleLine = true
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
    Row {
        Column(modifier = Modifier.weight(1f)) {
            AsteriskText {
                Text(
                    text = dictionaryString(R.string.newGame_numOfPlayers_header),
                )
            }
        }

        HorizontalSpacerD600()

        OutlinedTextField(
            modifier = Modifier.width(IntrinsicSize.Max),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = numOfPlayersState.value.orEmpty(),
            onValueChange = onNumOfPlayersUpdated,
            placeholder = {
                Text(
                    text = dictionaryString(
                        id = R.string.newGame_numOfPlayers_hint,
                        "min" to minPlayers.toString(),
                        "max" to maxPlayers.toString()
                    )
                )
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
            Text(text = dictionaryString(R.string.newGame_userName_header))
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = nameState.value.orEmpty(),
            onValueChange = onNameUpdated,
            placeholder = {
                Text(text = dictionaryString(R.string.newGame_userName_hint))
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
                    text = dictionaryString(R.string.newGame_videoCall_header),
                )
                Text(
                    text = dictionaryString(R.string.newGame_videCallDescription_text),
                    typography = OddOneOutTheme.typography.Body.B500
                )
            }

            HorizontalSpacerD600()

            IconButton(
                icon = SpyfallIcon.Info(dictionaryString(R.string.newGame_videoLinkInfo_a11y)),
                onClick = onVideoCallLinkInfoClicked
            )
        }

        Spacer(modifier = Modifier.height(Dimension.D600))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = link,
            onValueChange = onLinkUpdated,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
            singleLine = true
        )
    }
}

@Preview
@Composable
fun PreviewNewGameScreen() {
    Preview {
        var isSingleDevice by remember { mutableStateOf(false) }
        var packs by remember {
            mutableStateOf(
                listOf(
                    NewGamePackOption(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),
                    NewGamePackOption(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),
                    NewGamePackOption(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),
                    NewGamePackOption(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
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
            didCreationFail = false,
            didLoadFail = false,
            videoCallLinkState = FieldState.Valid(""),
            onVideoCallLinkUpdated = {},
            onNavigateBack = { },
            onCreateGameClicked = { },
            isLoadingCreation = false,
            onVideoCallLinkInfoClicked = { -> },
            onErrorDismissed = { -> },
            isOffline = false,
            onCreateYourOwnPackClicked = {},
            isCreateYourOwnNew = true
        )
    }
}

@Preview
@Composable
fun PreviewNewGameScreenOffline() {
    Preview {
        var packs by remember {
            mutableStateOf(
                listOf(
                    NewGamePackOption(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),
                    NewGamePackOption(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),
                    NewGamePackOption(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
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
            isSingleDevice = true,
            minPlayers = 3,
            maxPlayers = 9,
            minGameLength = 1,
            maxGameLength = 10,
            isFormValid = false,
            isVideoCallLinkEnabled = true,
            isLoadingPacks = false,
            onIsSingleDeviceUpdated = {},
            numOfPlayersState = FieldState.Valid(""),
            isSingleDeviceModeEnabled = true,
            onNumOfPlayersUpdated = { },
            packsState = FieldState.Valid(packs),
            didCreationFail = false,
            didLoadFail = false,
            videoCallLinkState = FieldState.Valid(""),
            onVideoCallLinkUpdated = {},
            onNavigateBack = { },
            onCreateGameClicked = { },
            isLoadingCreation = false,
            onVideoCallLinkInfoClicked = { -> },
            onErrorDismissed = { -> },
            isOffline = true,
            onCreateYourOwnPackClicked = {},
            isCreateYourOwnNew = true
        )
    }
}
