package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
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
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun InputField(
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

        VerticalSpacerD500()

        if (fieldState is FieldState.Invalid && (!hasFocus || shouldShowErrorWhileTyping)) {
            Text(
                text = fieldState.errorMessage,
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
