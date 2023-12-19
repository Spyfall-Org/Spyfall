package com.dangerfield.features.waitingroom.internal.changename

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
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.components.text.OutlinedTextField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.color.background

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
                .background(OddOneOutTheme.colorScheme.backgroundOverlay),
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
                Text(text = "Change Name")
            },
            content = {
                Column {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(text = "Enter a new name")
                        },
                        value = name,
                        onValueChange = onNameUpdated
                    )


                    val errorText = when {
                        isInvalidLength -> "Name must be between $minNameLength and $maxNameLength characters"
                        isNameTaken -> "That user name is already taken by another player."
                        else -> null
                    }

                    if (errorText != null) {
                        VerticalSpacerS800()

                        Text(
                            text = errorText,
                            color = OddOneOutTheme.colorScheme.textWarning,
                            typographyToken = OddOneOutTheme.typography.Label.L700
                        )
                    }
                }
            },
            bottomContent = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val isSubmitEnabled = !isNameTaken && !isInvalidLength
                    Button(
                        style = if (isSubmitEnabled) ButtonStyle.Filled else ButtonStyle.Outlined,
                        enabled = !isNameTaken && !isInvalidLength,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onChangeNameClicked(name) },
                        type = ButtonType.Regular
                    ) {
                        Text(text = "Submit")
                    }

                    VerticalSpacerS800()

                    Button(
                        modifier = Modifier.fillMaxWidth(), onClick = onDismissRequest
                    ) {
                        Text(text = "Cancel")
                    }
                }
            },
        )
    }
}

@Composable
@Preview
private fun ChangeNameDialogPreview() {
    PreviewContent {
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
    PreviewContent(isDarkMode = true) {
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