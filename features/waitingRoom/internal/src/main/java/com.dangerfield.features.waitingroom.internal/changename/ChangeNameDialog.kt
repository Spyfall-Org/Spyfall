package com.dangerfield.features.waitingroom.internal.changename

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
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.waitingroom.internal.R

@Composable
fun ChangeNameDialog(
    name: String,
    onNameUpdated: (String) -> Unit,
    onChangeNameClicked: (String) -> Unit,
    minNameLength: Int,
    maxNameLength: Int,
    modifier: Modifier = Modifier,
    isNameTaken: Boolean = false,
    isInvalidLength: Boolean = false,
    isLoading: Boolean = false,
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
                Text(text = dictionaryString(R.string.changeName_dialog_header))
            },
            content = {
                Column {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(text = dictionaryString(R.string.changeName_inputHint_label))
                        },
                        value = name,
                        onValueChange = onNameUpdated
                    )


                    val errorText = when {
                        isInvalidLength -> dictionaryString(
                            R.string.changeName_invalidLength_text,
                            "min" to minNameLength.toString(),
                            "max" to maxNameLength.toString()
                        )

                        isNameTaken -> dictionaryString(R.string.changeName_usernameTakenError_text)
                        else -> null
                    }

                    if (errorText != null) {
                        VerticalSpacerD800()

                        Text(
                            text = errorText,
                            colorResource = OddOneOutTheme.colors.textWarning,
                            typographyToken = OddOneOutTheme.typography.Label.L700
                        )
                    }
                }
            },
            bottomContent = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val isSubmitEnabled = !isNameTaken && !isInvalidLength
                    Button(
                        style = ButtonStyle.Background,
                        enabled = isSubmitEnabled,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onChangeNameClicked(name) },
                        type = ButtonType.Primary
                    ) {
                        Text(text = dictionaryString(R.string.app_submit_action))
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
private fun ChangeNameDialogPreview() {
    Preview {
        ChangeNameDialog(
            name = "",
            onNameUpdated = { },
            isNameTaken = false,
            isInvalidLength = false,
            onDismissRequest = { },
            onChangeNameClicked = { },
            minNameLength = 0,
            maxNameLength = 0,
        )
    }
}

@Composable
@Preview
private fun ChangeNameDialogPreviewDarkError() {
    Preview() {
        ChangeNameDialog(
            name = "",
            onNameUpdated = { },
            isNameTaken = false,
            isInvalidLength = true,
            onDismissRequest = { },
            onChangeNameClicked = { },
            minNameLength = 0,
            maxNameLength = 0,
        )
    }
}