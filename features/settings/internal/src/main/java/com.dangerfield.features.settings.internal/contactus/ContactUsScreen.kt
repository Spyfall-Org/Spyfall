package com.dangerfield.features.settings.internal.contactus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.radio.RadioButton
import com.dangerfield.libraries.ui.components.radio.RadioGroup
import com.dangerfield.libraries.ui.components.radio.rememberRadioButtonState
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.rememberRipple
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.spyfall.libraries.resources.R

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
                title = "Contact Us",
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
            .padding(PaddingValues(Spacing.S1000)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpacerS800()
        Text(
            text = "Your message has been sent to us, thank you for helping us improve the experience for everyone!",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        VerticalSpacerS800()
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

    ScrollingColumnWithFadingEdge(
        modifier = Modifier
            .padding(horizontal = Spacing.S800),
        state = scrollState
    ) {

        VerticalSpacerS800()

        Text(text = "What you would you like to contact us about?")
        AsteriskText {
            Text(
                text = "(Required)",
                typographyToken = OddOneOutTheme.typography.Body.B700
            )
        }
        VerticalSpacerS800()

        FormField(formFieldState = contactReasonFieldState) {
            Column {
                RadioGroup {
                    ContactReason.entries.forEach { reason ->
                        ContactTypeOption(
                            isSelected = contactReasonFieldState.backingValue == reason,
                            contactReason = reason,
                            onSelected = onContactReasonSelected
                        )
                    }
                }
                VerticalSpacerS800()
            }
        }

        FormField(formFieldState = nameFieldState) {
            Column {
                AsteriskText {
                    Text(text = "Name")
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nameFieldState.backingValue.orEmpty(),
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
                    Text(text = "Email")
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(emailFocusRequester),
                    value = emailFieldState.backingValue.orEmpty(),
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
                    Text(text = "Message")
                }
                OutlinedTextField(
                    modifier = Modifier
                        .heightIn(min = 200.dp)
                        .fillMaxWidth()
                        .focusRequester(messageFocusRequester),
                    value = messageFieldState.backingValue.orEmpty(),
                    onValueChange = onMessageUpdated,
                    singleLine = false,
                    placeholder = {
                        Text(text = "What would you like to tell us?")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onSubmitClicked()
                        }
                    )
                )
            }
        }

        VerticalSpacerS1200()

        if (isLoadingSubmit) {
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
                    onSubmitClicked()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit")
            }
        }

        // TODO add photo or video upload

        VerticalSpacerS1200()
    }
}


@Composable
private fun ContactTypeOption(
    isSelected: Boolean,
    contactReason: ContactReason,
    onSelected: (ContactReason) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val state = rememberRadioButtonState(isSelected)

    LaunchedEffect(state.selected) {
        if (state.selected) {
            onSelected(contactReason)
        }
    }

    Row(
        modifier = Modifier
            .clickable(interactionSource, rememberRipple()) {
                state.onClicked()
            }
            .fillMaxWidth()
            .padding(Spacing.S200)
    ) {
        Text(
            contactReason.name,
            modifier = Modifier.weight(1f),
            typographyToken = OddOneOutTheme.typography.Body.B700
        )

        RadioButton(state = state)
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
@ThemePreviews
private fun PreviewSettingsScreen() {
    PreviewContent {
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
    PreviewContent {
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
