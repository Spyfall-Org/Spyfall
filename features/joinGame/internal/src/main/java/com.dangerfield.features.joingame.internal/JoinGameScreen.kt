package com.dangerfield.features.joingame.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Spacing
import spyfallx.ui.components.Screen
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
fun JoinGameScreen(
    accessCode: String,
    userName: String,
    isLoading: Boolean,
    gameNotFound: Boolean,
    unresolvableError: UnresolvableError?,
    invalidNameLengthError: InvalidNameLengthError?,
    gameAlreadyStarted: Boolean,
    invalidAccessCodeLengthError: InvalidAccessCodeLengthError?,
    usernameTaken: Boolean,
    onJoinGameClicked: () -> Unit,
    onAccessCodeChanged: (String) -> Unit,
    onUserNameChanged: (String) -> Unit,
    onSomethingWentWrongDismissed: () -> Unit,
    onUpdateAppClicked: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box {
        JoinGameScreenContent(
            accessCode = accessCode,
            onAccessCodeChanged = onAccessCodeChanged,
            gameNotFound = gameNotFound,
            gameAlreadyStarted = gameAlreadyStarted,
            invalidAccessCodeLengthError = invalidAccessCodeLengthError,
            userName = userName,
            onUserNameChanged = onUserNameChanged,
            usernameTaken = usernameTaken,
            invalidNameLengthError = invalidNameLengthError,
            onJoinGameClicked = {
                focusManager.clearFocus()
                onJoinGameClicked()
            },
            isLoading = isLoading
        )

        if (unresolvableError != null) {
            JoinGameErrorDialog(
                onDismiss = onSomethingWentWrongDismissed,
                unresolvableError = unresolvableError,
                onUpdateClicked = onUpdateAppClicked
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun JoinGameScreenContent(
    accessCode: String,
    onAccessCodeChanged: (String) -> Unit,
    gameNotFound: Boolean,
    gameAlreadyStarted: Boolean,
    invalidAccessCodeLengthError: InvalidAccessCodeLengthError?,
    userName: String,
    onUserNameChanged: (String) -> Unit,
    usernameTaken: Boolean,
    invalidNameLengthError: InvalidNameLengthError?,
    onJoinGameClicked: () -> Unit,
    isLoading: Boolean
) {
    val (nameFocusRequester, accessCodeFocusRequester) = remember { FocusRequester.createRefs() }

    val scrollState = rememberScrollState()

    Screen(
        header = {
            Header(title = "Join Game")
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = Spacing.S1000)
        ) {
            Spacer(modifier = Modifier.height(Spacing.S1200))

            AccessCodeField(
                accessCodeFocusRequester,
                accessCode,
                onAccessCodeChanged,
                nameFocusRequester,
                gameNotFound,
                gameAlreadyStarted,
                invalidAccessCodeLengthError
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            NameField(
                nameFocusRequester,
                userName,
                onUserNameChanged,
                onJoinGameClicked,
                usernameTaken,
                invalidNameLengthError
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            Button(
                enabled = !isLoading,
                onClick = onJoinGameClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(25.dp),
                        color = SpyfallTheme.colorScheme.onAccent.color
                    )
                } else {
                    Text(text = "Join Game")
                }
            }
        }
    }
}

@Composable
private fun AccessCodeField(
    accessCodeFocusRequester: FocusRequester,
    accessCode: String,
    onAccessCodeChanged: (String) -> Unit,
    nameFocusRequester: FocusRequester,
    gameNotFound: Boolean,
    gameAlreadyStarted: Boolean,
    invalidAccessCodeLengthError: InvalidAccessCodeLengthError?
) {
    Text(text = "Access Code:", typographyToken = SpyfallTheme.typography.Heading.H900)
    Spacer(modifier = Modifier.height(Spacing.S600))

    OutlinedTextField(
        typographyToken = SpyfallTheme.typography.Label.L700,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(accessCodeFocusRequester),
        value = accessCode,
        onValueChange = onAccessCodeChanged,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                nameFocusRequester.requestFocus()
            }
        ),
        placeholder = {
            Text(text = "Enter the game access code:")
        },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(Spacing.S600))

    if (gameNotFound) {
        Text(
            text = "A game with that access code does not exist.",
            color = SpyfallTheme.colorScheme.textWarning,
            typographyToken = SpyfallTheme.typography.Label.L700
        )
    }

    if (gameAlreadyStarted) {
        Text(
            text = "This game has already started and can no longer be joined.",
            color = SpyfallTheme.colorScheme.textWarning,
            typographyToken = SpyfallTheme.typography.Label.L700
        )
    }

    if (invalidAccessCodeLengthError != null) {
        Text(
            text = "Access codes are ${invalidAccessCodeLengthError.requiredLength} characters long.",
            color = SpyfallTheme.colorScheme.textWarning,
            typographyToken = SpyfallTheme.typography.Label.L700
        )
    }
}

@Composable
private fun NameField(
    nameFocusRequester: FocusRequester,
    userName: String,
    onUserNameChanged: (String) -> Unit,
    onJoinGameClicked: () -> Unit,
    usernameTaken: Boolean,
    invalidNameLengthError: InvalidNameLengthError?
) {
    Text(text = "User Name:", typographyToken = SpyfallTheme.typography.Heading.H900)

    Spacer(modifier = Modifier.height(Spacing.S600))

    OutlinedTextField(
        typographyToken = SpyfallTheme.typography.Label.L700,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(nameFocusRequester),
        value = userName,
        onValueChange = onUserNameChanged,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onJoinGameClicked() }
        ),
        placeholder = {
            Text(text = "Pick a user name:")
        },
        singleLine = true
    )
    Spacer(modifier = Modifier.height(Spacing.S600))

    if (usernameTaken) {
        Text(
            text = "That user name is already taken by another player.",
            color = SpyfallTheme.colorScheme.textWarning,
            typographyToken = SpyfallTheme.typography.Label.L700
        )
    }

    if (invalidNameLengthError != null) {
        Text(
            text = "User names must be between ${invalidNameLengthError.min} and ${invalidNameLengthError.max} characters long.",
            color = SpyfallTheme.colorScheme.textWarning,
            typographyToken = SpyfallTheme.typography.Label.L700
        )
    }
}

@Preview
@Composable
fun PreviewJoinGameScreen() {
    PreviewContent() {
        var accessCode by remember { mutableStateOf("") }
        var userName by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        JoinGameScreen(
            accessCode = accessCode,
            userName = userName,
            isLoading = false,
            onJoinGameClicked = {
                isLoading = true
                coroutineScope.launch {
                    delay(5000)
                    isLoading = false
                }
            },
            onAccessCodeChanged = { accessCode = it },
            onUserNameChanged = { userName = it },
            gameNotFound = false,
            invalidNameLengthError = null,
            gameAlreadyStarted = false,
            invalidAccessCodeLengthError = null,
            usernameTaken = false,
            unresolvableError = null,
            onSomethingWentWrongDismissed = {},
            onUpdateAppClicked = {}
        )
    }
}

@Preview
@Composable
fun PreviewJoinGameScreenDark() {
    PreviewContent(isDarkMode = true) {
        var accessCode by remember { mutableStateOf("") }
        var userName by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        JoinGameScreen(
            accessCode = accessCode,
            userName = userName,
            isLoading = false,
            onJoinGameClicked = {
                isLoading = true
                coroutineScope.launch {
                    delay(5000)
                    isLoading = false
                }
            },
            onAccessCodeChanged = { accessCode = it },
            onUserNameChanged = { userName = it },
            gameNotFound = false,
            invalidNameLengthError = null,
            gameAlreadyStarted = false,
            invalidAccessCodeLengthError = null,
            usernameTaken = false,
            unresolvableError = null,
            onSomethingWentWrongDismissed = {},
            onUpdateAppClicked = {}
        )
    }
}

