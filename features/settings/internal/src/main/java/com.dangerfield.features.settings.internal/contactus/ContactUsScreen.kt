package com.dangerfield.features.settings.internal.contactus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.radio.EnumRadioGroup
import com.dangerfield.libraries.ui.components.radio.NewRadioButton
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.settings.internal.R

@Composable
fun ContactUsScreen(
    modifier: Modifier = Modifier,
    isLoadingSubmit: Boolean = false,
    isFormValid: Boolean,
    wasFormSubmittedSuccessfully: Boolean,
    didSubmitFail: Boolean,
    contactReasonFieldState: FieldState<ContactReason?>,
    nameFieldState: FieldState<String>,
    emailFieldState: FieldState<String>,
    messageFieldState: FieldState<String>,
    onNavigateBack: () -> Unit = { },
    onContactReasonSelected: (ContactReason) -> Unit = { },
    onSomethingWentWrongDismissed: () -> Unit,
    onNameUpdated: (String) -> Unit = { },
    onEmailUpdated: (String) -> Unit = { },
    onMessageUpdated: (String) -> Unit = { },
    onSubmitClicked: () -> Unit = { },
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Screen(
        modifier = modifier,
        topBar = {
            Header(
                title = dictionaryString(R.string.settings_contactUs_header),
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {

            AnimatedVisibility(
                visible = !wasFormSubmittedSuccessfully,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Form(
                    scrollState = scrollState,
                    contactReasonFieldState = contactReasonFieldState,
                    onContactReasonSelected = onContactReasonSelected,
                    nameFieldState = nameFieldState,
                    onNameUpdated = onNameUpdated,
                    emailFieldState = emailFieldState,
                    onEmailUpdated = onEmailUpdated,
                    messageFieldState = messageFieldState,
                    onMessageUpdated = onMessageUpdated,
                    isLoadingSubmit = isLoadingSubmit,
                    isFormValid = isFormValid,
                    focusManager = focusManager,
                    onSubmitClicked = onSubmitClicked
                )
            }

            AnimatedVisibility(
                visible = wasFormSubmittedSuccessfully,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SuccessMessage()
            }

            if (didSubmitFail) {
                ContactUsErrorDialog(onDismiss = onSomethingWentWrongDismissed)
            }
        }
    }
}

@Composable
private fun SuccessMessage() {
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_animation))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(Dimension.D1000)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpacerD800()
        Text(
            text = dictionaryString(R.string.contactUs_submitSuccessMessage_text),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        VerticalSpacerD800()
        LottieAnimation(
            composition = lottieComposition,
            isPlaying = true,
            speed = 1.5f
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Form(
    scrollState: ScrollState,
    contactReasonFieldState: FieldState<ContactReason?>,
    onContactReasonSelected: (ContactReason) -> Unit,
    nameFieldState: FieldState<String>,
    onNameUpdated: (String) -> Unit,
    emailFieldState: FieldState<String>,
    onEmailUpdated: (String) -> Unit,
    messageFieldState: FieldState<String>,
    onMessageUpdated: (String) -> Unit,
    isLoadingSubmit: Boolean,
    isFormValid: Boolean,
    focusManager: FocusManager,
    onSubmitClicked: () -> Unit
) {

    val (emailFocusRequester, messageFocusRequester) = FocusRequester.createRefs()

    Column(
        modifier = Modifier
            .padding(horizontal = Dimension.D800)
            .verticalScroll(scrollState)
        ,
    ) {

        VerticalSpacerD800()

        Text(text = dictionaryString(R.string.contactUs_reasonPrompt_text))
        AsteriskText {
            Text(
                text = "(${dictionaryString(R.string.app_required_label)})",
                typographyToken = OddOneOutTheme.typography.Body.B700
            )
        }
        VerticalSpacerD800()

        FormField(formFieldState = contactReasonFieldState) {
            Column {
                EnumRadioGroup(
                    enumEntries = ContactReason.entries,
                    onItemSelected = onContactReasonSelected
                ) {
                    ContactTypeOption(
                        contactReason = it,
                        selected = contactReasonFieldState.value == it,
                    )
                }

                VerticalSpacerD800()
            }
        }

        FormField(formFieldState = nameFieldState) {
            Column {
                AsteriskText {
                    Text(text = dictionaryString(R.string.contactUs_nameField_header))
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nameFieldState.value.orEmpty(),
                    onValueChange = onNameUpdated,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { emailFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }
        }

        FormField(formFieldState = emailFieldState) {
            Column {
                AsteriskText {
                    Text(text = dictionaryString(R.string.contactUs_emailField_header))
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(emailFocusRequester),
                    value = emailFieldState.value.orEmpty(),
                    onValueChange = onEmailUpdated,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { messageFocusRequester.requestFocus() }
                    )
                )
            }
        }

        FormField(formFieldState = messageFieldState) {
            Column {
                AsteriskText {
                    Text(text = dictionaryString(R.string.contactUs_messageField_header))
                }
                OutlinedTextField(
                    modifier = Modifier
                        .heightIn(min = 200.dp)
                        .fillMaxWidth()
                        .focusRequester(messageFocusRequester),
                    value = messageFieldState.value.orEmpty(),
                    onValueChange = onMessageUpdated,
                    singleLine = false,
                    placeholder = {
                        Text(
                            text = dictionaryString(R.string.contactUs_messageField_hint),
                            maxLines = 5
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )
            }
        }

        VerticalSpacerD1200()

        if (isLoadingSubmit) {
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
                    onSubmitClicked()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(R.string.app_submit_action))
            }
        }

        // TODO add photo or video upload

        VerticalSpacerD1200()
    }
}


@Composable
private fun ContactTypeOption(
    contactReason: ContactReason,
    selected: Boolean,
) {
    val reasonString = when (contactReason) {
        ContactReason.Question -> dictionaryString(R.string.contactUs_question_label)
        ContactReason.Issue -> dictionaryString(R.string.contactUs_issue_label)
        ContactReason.Feedback -> dictionaryString(R.string.contactUs_feedback_label)
        ContactReason.Suggestion -> dictionaryString(R.string.contactUs_suggestion_label)
        ContactReason.Other -> dictionaryString(R.string.contactUs_other_label)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimension.D600)
    ) {
        Text(
            reasonString,
            modifier = Modifier.weight(1f),
            typographyToken = OddOneOutTheme.typography.Body.B700
        )

        NewRadioButton(
            selected = selected,
            onClick = null
        )
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
                    typographyToken = OddOneOutTheme.typography.Body.B500,
                    colorResource = OddOneOutTheme.colors.textWarning
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSettingsScreen() {
    Preview {
        ContactUsScreen(
            isLoadingSubmit = false,
            isFormValid = false,
            nameFieldState = FieldState.Valid(""),
            emailFieldState = FieldState.Valid(""),
            messageFieldState = FieldState.Valid(""),
            onNavigateBack = { -> },
            onSubmitClicked = { -> },
            contactReasonFieldState = FieldState.Valid(null),
            onContactReasonSelected = { },
            onNameUpdated = { },
            onEmailUpdated = { },
            onMessageUpdated = { },
            wasFormSubmittedSuccessfully = false,
            didSubmitFail = false,
            onSomethingWentWrongDismissed = { -> }
        )
    }
}

@Composable
@Preview
private fun PreviewSuccess() {
    Preview {
        ContactUsScreen(
            isLoadingSubmit = false,
            isFormValid = false,
            nameFieldState = FieldState.Valid(""),
            emailFieldState = FieldState.Valid(""),
            messageFieldState = FieldState.Valid(""),
            onNavigateBack = { -> },
            onSubmitClicked = { -> },
            contactReasonFieldState = FieldState.Valid(null),
            onContactReasonSelected = { },
            onNameUpdated = { },
            onEmailUpdated = { },
            onMessageUpdated = { },
            wasFormSubmittedSuccessfully = true,
            didSubmitFail = false,
            onSomethingWentWrongDismissed = { -> }
        )
    }
}
