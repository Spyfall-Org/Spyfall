package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD100
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun InputField(
    title: String,
    fieldState: FieldState<String>,
    onFieldUpdated: (String) -> Unit,
    subtitle: String? = null,
    focusRequester: FocusRequester = FocusRequester(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    hint: String? = null,
    isRequired: Boolean = false,
    hideErrorWhen: (text: String, hasFocus: Boolean) -> Boolean = {_, hasFocus -> hasFocus },
    onFocusChanged: (Boolean) -> Unit = {},
    onErrorShown: () -> Unit = {}
) {
    var hasFocus by remember { mutableStateOf(false) }

    val shouldShowError = remember(fieldState, hasFocus) {
        val shouldHide = hideErrorWhen(fieldState.value.orEmpty(), hasFocus)
        fieldState is FieldState.Invalid && !shouldHide
    }

    LaunchedEffect(shouldShowError) {
        if (shouldShowError) {
            onErrorShown()
        }
    }

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

        if (subtitle != null) {
            VerticalSpacerD100()
            Text(
                text = subtitle,
                typography = OddOneOutTheme.typography.Body.B500
            )
            VerticalSpacerD800()
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

        VerticalSpacerD500()

        if (shouldShowError) {
            Text(
                text = (fieldState as? FieldState.Invalid)?.errorMessage ?: "",
                typography = OddOneOutTheme.typography.Body.B500,
                colorResource = OddOneOutTheme.colors.textWarning
            )
        }
    }
}

@Composable
@Preview
private fun PreviewInputField() {
    Preview {
        InputField(
            title = "Title",
            fieldState = FieldState.Valid(""),
            onFieldUpdated = {},
            focusRequester = FocusRequester(),
            hint = "Hint",
            isRequired = true
        )
    }
}

@Composable
@Preview
private fun PreviewInputFieldNotRequired() {
    Preview {
        InputField(
            title = "Title",
            fieldState = FieldState.Valid(""),
            onFieldUpdated = {},
            focusRequester = FocusRequester(),
            hint = "Hint",
            isRequired = false
        )
    }
}

@Composable
@Preview
private fun PreviewInputFieldNotRequiredWSub() {
    Preview {
        InputField(
            title = "Title",
            subtitle = "Some smaller descriptive text",
            fieldState = FieldState.Valid(""),
            onFieldUpdated = {},
            focusRequester = FocusRequester(),
            hint = "Hint",
            isRequired = false
        )
    }
}

