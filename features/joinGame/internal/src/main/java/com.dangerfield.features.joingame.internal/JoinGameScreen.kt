package com.dangerfield.features.joingame.internal

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.InputField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.oddoneoout.features.joingame.internal.R
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
                title = dictionaryString(R.string.joinGame_joinGameScreen_header),
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = Dimension.D1000)
        ) {
            VerticalSpacerD1200()

            InputField(
                title = dictionaryString(R.string.joinGame_accessCode_header),
                hint = dictionaryString(
                    R.string.joinGame_accessCode_hint,
                    "length" to accessCodeLength.toString()
                ),
                shouldShowErrorWhileTyping = false,
                focusRequester = accessCodeFocusRequester,
                fieldState = accessCodeState,
                keyboardActions = KeyboardActions {
                    nameFocusRequester.requestFocus()
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                onFieldUpdated = onAccessCodeChanged
            )

            VerticalSpacerD1200()

            // TODO use username generation an collect analytics around usage
            InputField(
                title = dictionaryString(R.string.joinGame_usernameField_header),
                hint = dictionaryString(R.string.joinGame_userNameField_hint),
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

            VerticalSpacerD1200()

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
                    style = ButtonStyle.Background,
                    enabled = isFormValid,
                ) {
                    Text(text = dictionaryString(R.string.joinGame_join_action))
                }
            }

            VerticalSpacerD1200()
        }
    }
}

@Preview
@Composable
fun PreviewJoinGameScreen() {
    Preview() {
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
