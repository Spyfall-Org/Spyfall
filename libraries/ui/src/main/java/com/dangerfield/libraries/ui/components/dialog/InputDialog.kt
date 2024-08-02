package com.dangerfield.libraries.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.R

@Composable
fun InputDialog(
    input: FieldState<String>,
    onInputChanged: (String) -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    primaryButtonText: String = "Add",
    title: String,
    inputHint: String? = null,
    onDismissRequest: () -> Unit,
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(OddOneOutTheme.colors.backgroundOverlay.color),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        BasicDialog(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            topContent = {
                Text(text = title)
            },
            content = {
                Column {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = {
                            inputHint?.let {
                                Text(text = it)
                            }
                        },
                        value = input.value.orEmpty(),
                        onValueChange = onInputChanged
                    )

                    val errorText = input.error

                    if (errorText != null) {
                        VerticalSpacerD800()

                        Text(
                            text = errorText,
                            colorResource = OddOneOutTheme.colors.textWarning,
                            typography = OddOneOutTheme.typography.Label.L700
                        )
                    }
                }
            },
            bottomContent = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        style = ButtonStyle.Background,
                        enabled = input is FieldState.Valid,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSubmit(input.value.orEmpty()) },
                        type = ButtonType.Primary
                    ) {
                        Text(text = primaryButtonText)
                    }

                    VerticalSpacerD800()

                    Button(
                        type = ButtonType.Secondary,
                        modifier = Modifier.fillMaxWidth(), onClick = onDismissRequest
                    ) {
                        Text(text = dictionaryString(R.string.app_cancel_action))
                    }
                }
            },
        )
    }
}

@Composable
@Preview
private fun DialogPreview() {
    Preview {
        InputDialog(
            input = FieldState.Idle(""),
            onInputChanged = { },
            onDismissRequest = { },
            onSubmit = { },
            isLoading = false,
            primaryButtonText = "Add",
            title = "Dialog Title",
            inputHint = "this is the hint",
        )
    }
}

@Composable
@Preview
private fun DialogPreviewError() {
    Preview {
        InputDialog(
            input = FieldState.Error("", errorMessage = "This is the error"),
            onInputChanged = { },
            onDismissRequest = { },
            onSubmit = { },
            isLoading = false,
            primaryButtonText = "Primary text",
            title = "Dialog Title",
            inputHint = "this is the hint",
        )
    }
}