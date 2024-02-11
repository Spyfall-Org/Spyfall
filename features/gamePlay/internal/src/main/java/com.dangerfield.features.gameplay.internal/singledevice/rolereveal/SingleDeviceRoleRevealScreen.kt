package com.dangerfield.features.gameplay.internal.singledevice.rolereveal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.ads.ui.AdBanner
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.singledevice.EndGameDialog
import com.dangerfield.features.gameplay.internal.singledevice.EndOrGoBackDialog
import com.dangerfield.features.gameplay.internal.ui.GamePlayGrid
import com.dangerfield.features.gameplay.internal.ui.RoleCard
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.pulsate
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.gameplay.internal.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import spyfallx.ui.thenIf

@Composable
fun SingleDevicePlayerRoleScreen(
    currentPlayer: DisplayablePlayer?,
    location: String?,
    onNextPlayerClicked: () -> Unit,
    isLastPlayer: Boolean,
    isFirstPlayer: Boolean,
    locationOptions: List<String>,
    nameFieldState: FieldState<String?>,
    onStartGameClicked: () -> Unit,
    onEndGameClicked: () -> Unit,
    onPreviousPlayerClicked: () -> Unit,
    onNameUpdated: (String) -> Unit
) {

    val scrollState = rememberScrollState()
    var shouldShowExitDialog by remember { mutableStateOf(false) }
    var isRoleHidden by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var hasPlayerClickedShow by remember { mutableStateOf(false) }

    BackHandler {
        shouldShowExitDialog = !shouldShowExitDialog
    }

    Box {
        RoleRevealScreenContent(
            currentPlayer = currentPlayer,
            isLastPlayer = isLastPlayer,
            location = location,
            scrollState = scrollState,
            nameFieldState = nameFieldState,
            onNameUpdated = onNameUpdated,
            locationOptions = locationOptions,
            onStartGameClicked = onStartGameClicked,
            hasPlayerClickedShow = hasPlayerClickedShow,
            isRoleVisible = !isRoleHidden,
            hideRoleField = { isRoleHidden = true },
            showRoleField = {
                isRoleHidden = false
                hasPlayerClickedShow = true
            },
            loadNextPlayer = {
                isRoleHidden = true
                coroutineScope.launch {
                    scrollState.animateScrollTo(0)
                    hasPlayerClickedShow = false
                    // wait for role to hide
                    delay(100)
                    onNextPlayerClicked()
                }
            },
        )

        if (shouldShowExitDialog) {
            when {
                !isFirstPlayer -> {
                    EndOrGoBackDialog(
                        onDismissRequest = { shouldShowExitDialog = false },
                        onEndGame = onEndGameClicked,
                        onGoBack = {
                            isRoleHidden = true
                            shouldShowExitDialog = false
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                                // wait for role to hide
                                delay(100)
                                onPreviousPlayerClicked()
                            }
                        }
                    )
                }

                else -> {
                    EndGameDialog(
                        onDismissRequest = { shouldShowExitDialog = false },
                        onEndGame = {
                            onEndGameClicked()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleRevealScreenContent(
    currentPlayer: DisplayablePlayer?,
    isLastPlayer: Boolean,
    location: String?,
    scrollState: ScrollState,
    isRoleVisible: Boolean,
    hasPlayerClickedShow: Boolean,
    nameFieldState: FieldState<String?>,
    onNameUpdated: (String) -> Unit,
    locationOptions: List<String>,
    onStartGameClicked: () -> Unit,
    hideRoleField: () -> Unit,
    showRoleField: () -> Unit,
    loadNextPlayer: () -> Unit
) {

    val focusManager = LocalFocusManager.current

    Screen(
        topBar = {
            AdBanner(ad = OddOneOutAd.RoleRevealBanner)
        }
    ) { padding ->

        ScrollingColumnWithFadingEdge(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.S1100),
            horizontalAlignment = CenterHorizontally
        ) {

            // TODO add horizontal pager?
            VerticalSpacerS1200()

            if (currentPlayer != null) {

                Text(
                    text = dictionaryString(
                        R.string.singleDevice_handToPlayer_header,
                        mapOf("name" to currentPlayer.name)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typographyToken = OddOneOutTheme.typography.Display.D1100
                )

                VerticalSpacerS800()

                if (isLastPlayer) {
                    Text(
                        text = dictionaryString(R.string.roleReveal_starterInstructions_text),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                } else {
                    Text(
                        text = dictionaryString(R.string.roleReveal_instructions_text),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                }

                VerticalSpacerS1200()

                RoleCard(
                    modifier = Modifier.thenIf(!hasPlayerClickedShow) { pulsate() },
                    role = currentPlayer.role,
                    isTheOddOneOut = currentPlayer.isOddOneOut,
                    location = location,
                    text = if (currentPlayer.isOddOneOut) dictionaryString(R.string.roleReveal_dontGetFoundOut_text) else null,
                    isVisible = isRoleVisible,
                    onHideShowClicked = {
                        if (!isRoleVisible) showRoleField() else hideRoleField()
                    }
                )

                VerticalSpacerS1200()

                Text(
                    text = dictionaryString(R.string.roleReveal_changeName_header),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                VerticalSpacerS500()

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = nameFieldState.value.orEmpty(),
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
                        Text(text = dictionaryString(R.string.roleReveal_changeName_hint))
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
                        text = dictionaryString(R.string.roleReveal_changeNameSuccess_text),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                        typographyToken = OddOneOutTheme.typography.Body.B500,
                        colorPrimitive = ColorPrimitive.MintyFresh300
                    )
                }

                VerticalSpacerS1200()

                Text(text = dictionaryString(R.string.roleReveal_locations_header))

                VerticalSpacerS500()
                GamePlayGrid(
                    items = locationOptions,
                    isDisplayingForSelection = false,
                    isClickEnabled = false
                )

                VerticalSpacerS1200()

                val areButtonsEnabled = hasPlayerClickedShow &&
                    (nameFieldState is FieldState.Valid || nameFieldState is FieldState.Idle)

                if (isLastPlayer) {
                    Button(
                        onClick = onStartGameClicked,
                        modifier = Modifier.fillMaxWidth(),
                        style = ButtonStyle.Filled,
                        enabled = areButtonsEnabled,
                    ) {
                        Text(text = dictionaryString(R.string.roleReveal_startGame_action))
                    }
                } else {
                    Button(
                        onClick = loadNextPlayer,
                        style = ButtonStyle.Filled,
                        enabled = areButtonsEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = dictionaryString(R.string.roleReveal_nextPlayer_action))
                    }
                }

                VerticalSpacerS1200()

            } else {
                Text(
                    text = dictionaryString(R.string.roleReveal_loading_text),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typographyToken = OddOneOutTheme.typography.Display.D1100
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSingleDevicePlayerRoleScreen() {
    PreviewContent {
        SingleDevicePlayerRoleScreen(
            currentPlayer = DisplayablePlayer(
                name = "Player 1",
                id = "",
                role = "The Odd One Out",
                isFirst = false,
                isOddOneOut = true
            ),
            location = "Something",
            onNextPlayerClicked = { -> },
            isLastPlayer = false,
            isFirstPlayer = false,
            onStartGameClicked = { -> },
            nameFieldState = FieldState.Valid("Jane"),
            onNameUpdated = { },
            locationOptions = listOf("Bank", "School", "Hospital", "Park", "Mall", "Restaurant"),
            onEndGameClicked = { -> },
            onPreviousPlayerClicked = { -> },
        )
    }
}