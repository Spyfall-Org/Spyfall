package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD100
import com.dangerfield.libraries.ui.VerticalSpacerD300
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.ErrorBehavior
import com.dangerfield.libraries.ui.components.FormField
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun InputField(
    title: String?,
    fieldState: FieldState<String>,
    onFieldUpdated: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    focusRequester: FocusRequester = FocusRequester(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    hint: String? = null,
    isRequired: Boolean = false,
    errorBehavior: ErrorBehavior = ErrorBehavior.Show,
    onFocusChanged: (Boolean) -> Unit = {},
) {

    FormField(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .focusRequester(focusRequester),
        formFieldState = fieldState,
        visible = true,
        errorBehavior = errorBehavior,
        onFocusChanged = onFocusChanged,
    ) {
        Column(modifier = Modifier) {
            if (title != null) {
                if (isRequired) {
                    AsteriskText {
                        Text(text = title)
                    }
                } else {
                    Text(text = title)
                }
            }

            if (subtitle != null) {
                VerticalSpacerD100()
                Text(
                    text = subtitle,
                    typography = OddOneOutTheme.typography.Body.B500
                )
                VerticalSpacerD300()
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardActions = keyboardActions,
                keyboardOptions = keyboardOptions,
                value = fieldState.value.orEmpty(),
                onValueChange = onFieldUpdated,
                placeholder = {
                    hint?.let { Text(text = it) }
                },
                singleLine = true
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
private fun PreviewInputFieldError() {
    Preview {
        InputField(
            title = "Title",
            fieldState = FieldState.Invalid("Bad Input", "This input is bad, do better."),
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

