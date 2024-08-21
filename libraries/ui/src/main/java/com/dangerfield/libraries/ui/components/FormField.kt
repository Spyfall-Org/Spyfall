package com.dangerfield.libraries.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

/**
 * A wrapper around a field of any kind, could be text, switch, anything
 * Mainly helps with showing errors
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FormField(
    formFieldState: FieldState<*>,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    errorBehavior: ErrorBehavior = ErrorBehavior.Show,
    onFocusChanged: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    val errorBringIntoViewRequester = remember { BringIntoViewRequester() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible
    ) {
        Column {
            Box(modifier = Modifier
                .onFocusChanged {
                hasFocus = it.hasFocus
                onFocusChanged(it.hasFocus)
            }) {
                content()
            }

            val errorText = formFieldState.error
            val textModifier = Modifier.bringIntoViewRequester(errorBringIntoViewRequester)

            val shouldShowError = when (errorBehavior) {
                ErrorBehavior.Show -> errorText != null
                ErrorBehavior.ShowIfFocused -> errorText != null && hasFocus
                ErrorBehavior.ShowIfNotFocused -> errorText != null && !hasFocus
                ErrorBehavior.DontShow -> false
            }

            if (shouldShowError && errorText!= null) {

                VerticalSpacerD500()

                LaunchedEffect(errorBringIntoViewRequester) {
                    errorBringIntoViewRequester.bringIntoView()
                }

                Text(
                    modifier = textModifier,
                    text = errorText,
                    typography = OddOneOutTheme.typography.Body.B500,
                    colorResource = OddOneOutTheme.colors.textWarning
                )
            }
        }
    }
}

enum class ErrorBehavior {
    /**
     * If the error exists, show it
     */
    Show,
    /**
     * If the error exists and the field is not focused, show it
     */
    ShowIfNotFocused,
    /**
     * If the error exists and the field is focused, show it
     */
    ShowIfFocused,
    /**
     * Never show the error
     */
    DontShow
}


@Composable
@Preview
private fun PreviewFormField() {
    Preview {
        FormField(
            formFieldState = FieldState.Valid(""),
            visible = true,
            errorBehavior = ErrorBehavior.Show,
        ) {
            Text(text = "This is a valid field with just text inside of it")
        }
    }
}

@Composable
@Preview
private fun PreviewFormFieldInvalid() {
    Preview {
        FormField(
            formFieldState = FieldState.Invalid("", "This is an error message"),
            visible = true,
            errorBehavior = ErrorBehavior.Show,
        ) {
            Text(text = "This is a valid field with just text inside of it")
        }
    }
}