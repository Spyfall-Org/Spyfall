package com.dangerfield.features.joingame.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

// TODO add error messages while typing
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
    onUpdateAppClicked: () -> Unit,
    onNavigateBack: () -> Unit
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
            isLoading = isLoading,
            onNavigateBack = onNavigateBack
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
    isLoading: Boolean,
    onNavigateBack: () -> Unit
) {
    val (nameFocusRequester, accessCodeFocusRequester) = remember { FocusRequester.createRefs() }

    val scrollState = rememberScrollState()

    Screen(
        topBar = {
            Header(
                title = "Join Game",
                onNavigateBack = onNavigateBack
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

            // TODO use username generation an collect analytics around usage
            NameField(
                nameFocusRequester,
                userName,
                onUserNameChanged,
                onJoinGameClicked,
                usernameTaken,
                invalidNameLengthError
            )

            Spacer(modifier = Modifier.height(Spacing.S1200))

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // TODO change to add disabled state
                Button(
                    onClick = onJoinGameClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Join Game")
                }
            }

            VerticalSpacerS1200()
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

    AsteriskText {
        Text(text = "Access Code:")
    }

    Spacer(modifier = Modifier.height(Spacing.S600))

    OutlinedTextField(
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
            Text(text = "6 character game code")
        },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(Spacing.S600))

    if (gameNotFound) {
        Text(
            text = "A game with that access code does not exist.",
            color = OddOneOutTheme.colorScheme.textWarning,
            typographyToken = OddOneOutTheme.typography.Label.L700
        )
    }

    if (gameAlreadyStarted) {
        Text(
            text = "This game has already started and can no longer be joined.",
            color = OddOneOutTheme.colorScheme.textWarning,
            typographyToken = OddOneOutTheme.typography.Label.L700
        )
    }

    if (invalidAccessCodeLengthError != null) {
        val text = if (accessCode.isNotEmpty()) {
            "Access codes are ${invalidAccessCodeLengthError.requiredLength} character long."
        } else {
            "Please fill out the access code."
        }
        Text(
            text = text,
            color = OddOneOutTheme.colorScheme.textWarning,
            typographyToken = OddOneOutTheme.typography.Label.L700
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
    AsteriskText {
        Text(text = "User Name:")
    }
    Spacer(modifier = Modifier.height(Spacing.S600))

    OutlinedTextField(
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
            color = OddOneOutTheme.colorScheme.textWarning,
            typographyToken = OddOneOutTheme.typography.Label.L700
        )
    }

    if (invalidNameLengthError != null) {
        Text(
            text = "User names must be between ${invalidNameLengthError.min} and ${invalidNameLengthError.max} characters long.",
            color = OddOneOutTheme.colorScheme.textWarning,
            typographyToken = OddOneOutTheme.typography.Label.L700
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
            onUpdateAppClicked = {},
            onNavigateBack = {}
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
            onUpdateAppClicked = {},
            onNavigateBack = {}
        )
    }
}

