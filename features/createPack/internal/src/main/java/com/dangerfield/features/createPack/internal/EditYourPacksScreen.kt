package com.dangerfield.features.createPack.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.components.ErrorBehavior
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.InputField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.isValid
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditYourPacksScreen(
    modifier: Modifier = Modifier,
    nameState: FieldState<String>,
    onNameChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val (nameFocusRequester) = remember { FocusRequester.createRefs() }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
    }

    Screen(
        modifier = modifier,
        topBar = {
            Header(
                navigationIcon = SpyfallIcon.Close("Close"),
                onNavigateBack = onNavigateBack,
                title = "Edit Your Packs",
                titleAlignment = Alignment.CenterHorizontally,
                typographyToken = OddOneOutTheme.typography.Heading.H800
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = Dimension.D1000),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            VerticalSpacerD1200()

            Text(text = "Give your pack a name")

            VerticalSpacerD1200()

            InputField(
                modifier = Modifier.fillMaxWidth(),
                title = null,
                hint = "Enter a name",
                errorBehavior = ErrorBehavior.Show,
                focusRequester = nameFocusRequester,
                fieldState = nameState,
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                onFieldUpdated = onNameChanged
            )

            VerticalSpacerD1200()


            VerticalSpacerD1200()

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onNextClicked()
                },
                modifier = Modifier.fillMaxWidth(),
                style = ButtonStyle.Background,
                enabled = nameState.isValid(),
            ) {
                Text(text = "Next Step")
            }

            VerticalSpacerD1200()
        }
    }
}

@Preview
@Composable
private fun PreviewEditYourPacksScreen() {
    Preview {
        EditYourPacksScreen(
            nameState = FieldState.Idle(""),
            onNameChanged = {},
            onNextClicked = { },
            onNavigateBack = {}
        )
    }
}
