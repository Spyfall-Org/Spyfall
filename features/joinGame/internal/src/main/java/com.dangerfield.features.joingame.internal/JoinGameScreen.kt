package com.dangerfield.features.joingame.internal

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JoinGameScreen(
    accessCodeState: FieldState<String>,
    userNameState: FieldState<String>,
    isFormValid: Boolean,
    isLoading: Boolean,
    accessCodeLength: Int,
    unresolvableError: UnresolvableError?,
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
            accessCodeState = accessCodeState,
            userNameState = userNameState,
            isLoading = isLoading,
            onAccessCodeChanged = onAccessCodeChanged,
            onUserNameChanged = onUserNameChanged,
            onJoinGameClicked = {
                focusManager.clearFocus()
                onJoinGameClicked()
            },
            accessCodeLength = accessCodeLength,
            onNavigateBack = onNavigateBack,
            isFormValid = isFormValid
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
    accessCodeState: FieldState<String>,
    userNameState: FieldState<String>,
    accessCodeLength: Int,
    isFormValid: Boolean,
    isLoading: Boolean,
    onAccessCodeChanged: (String) -> Unit,
    onUserNameChanged: (String) -> Unit,
    onJoinGameClicked: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val (nameFocusRequester, accessCodeFocusRequester) = remember { FocusRequester.createRefs() }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        accessCodeFocusRequester.requestFocus()
    }

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
            VerticalSpacerS1200()

            InputField(
                title = "Access Code:",
                hint = "Enter the $accessCodeLength digit code",
                shouldShowErrorWhileTyping = false,
                focusRequester = accessCodeFocusRequester,
                fieldState = accessCodeState,
                keyboardActions = KeyboardActions {
                    nameFocusRequester.requestFocus()
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                onFieldUpdated = onAccessCodeChanged
            )

            VerticalSpacerS1200()

            // TODO use username generation an collect analytics around usage
            InputField(
                title = "Username:",
                hint = "Pick a username",
                shouldShowErrorWhileTyping = true,
                focusRequester = nameFocusRequester,
                fieldState = userNameState,
                onFieldUpdated = onUserNameChanged,
                keyboardActions = KeyboardActions {
                    if (isFormValid) {
                        onJoinGameClicked()
                    } else {
                        focusManager.clearFocus()
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            )

            VerticalSpacerS1200()

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    onClick = onJoinGameClicked,
                    modifier = Modifier.fillMaxWidth(),
                    style = if (isFormValid) ButtonStyle.Filled else ButtonStyle.Outlined,
                    enabled = isFormValid,
                ) {
                    Text(text = "Join Game")
                }
            }

            VerticalSpacerS1200()
        }
    }
}

@Composable
private fun InputField(
    title: String,
    fieldState: FieldState<String>,
    onFieldUpdated: (String) -> Unit,
    focusRequester: FocusRequester,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    hint: String? = null,
    isRequired: Boolean = false,
    shouldShowErrorWhileTyping: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    var hasFocus by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                hasFocus = it.hasFocus
                onFocusChanged(it.hasFocus)
            }
    ) {

        if (isRequired) {
            AsteriskText {
                Text(text = title)
            }
        } else {
            Text(text = title)
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            value = fieldState.value.orEmpty(),
            onValueChange = onFieldUpdated,
            placeholder = {
                hint?.let { Text(text = it) }
            },
            singleLine = true
        )

        VerticalSpacerS500()

        if (fieldState is FieldState.Invalid && (!hasFocus || shouldShowErrorWhileTyping)) {
            Text(
                text = fieldState.errorMessage,
                typographyToken = OddOneOutTheme.typography.Body.B500,
                color = OddOneOutTheme.colorScheme.textWarning
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewJoinGameScreen() {
    PreviewContent() {
        var accessCode by remember { mutableStateOf("") }
        var userName by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        JoinGameScreen(
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
            unresolvableError = null,
            onSomethingWentWrongDismissed = {},
            onUpdateAppClicked = {},
            onNavigateBack = {},
            accessCodeState = FieldState.Valid(""),
            userNameState = FieldState.Valid(""),
            isFormValid = true,
            accessCodeLength = 6
        )
    }
}
