package com.dangerfield.features.consent.internal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.HorizontalSpacerS500
import com.dangerfield.libraries.ui.Sizes
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.checkbox.Checkbox
import com.dangerfield.libraries.ui.components.checkbox.rememberCheckboxState
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.makeLink
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.consent.internal.R

@Composable
fun InHouseConsentScreen(
    onAcceptClicked: () -> Unit = {},
    onTermsOfServiceClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
) {
    Screen { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Sizes.S1100),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val checkboxState = rememberCheckboxState(initialState = false)
            val focusManager = LocalFocusManager.current

            VerticalSpacerS800()

            Text(
                text = dictionaryString(id = R.string.termsOfService_screen_header),
                typographyToken = OddOneOutTheme.typography.Display.D1000
            )

            VerticalSpacerS1200()

            Text(
                text = dictionaryString(R.string.termsOfService_description_text),
                typographyToken = OddOneOutTheme.typography.Body.B700
            )

            VerticalSpacerS800()

            LinksSection(
                text = dictionaryString(
                    R.string.termsOfService_links_text,
                    "termsOfService" to dictionaryString(id = R.string.app_termsOfService_text),
                    "privacyPolicy" to dictionaryString(id = R.string.app_privacyPolicy_text)
                ),
                onTermsOfServiceClicked = onTermsOfServiceClicked,
                onPrivacyPolicyClicked = onPrivacyPolicyClicked,
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        checkboxState.onClicked()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(state = checkboxState)
                HorizontalSpacerS500()
                Text(
                    text = dictionaryString(R.string.termsOfService_confirmation_text),
                    typographyToken = OddOneOutTheme.typography.Body.B600
                )
            }

            VerticalSpacerS1200()

            Button(
                style = ButtonStyle.Filled,
                enabled = checkboxState.checked,
                onClick = {
                    focusManager.clearFocus()
                    if (checkboxState.checked) {
                        onAcceptClicked()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(R.string.termsOfService_accept_action))
            }

            VerticalSpacerS1200()
        }
    }
}

@Composable
fun LinksSection(
    text: String,
    onTermsOfServiceClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val termsOfServiceText = dictionaryString(id = R.string.app_termsOfService_text)
    val privacyPolicyText = dictionaryString(id = R.string.app_privacyPolicy_text)

    val annotatedString = text
        .makeLink(
            linkText = termsOfServiceText,
            annotation = termsOfServiceText to "link",
            style = SpanStyle(
                color = OddOneOutTheme.colorScheme.accent.color,
                fontWeight = FontWeight.ExtraBold,
                textDecoration = TextDecoration.Underline
            )
        )
        .makeLink(
            linkText = privacyPolicyText,
            annotation = privacyPolicyText to "link",
            style = SpanStyle(
                color = OddOneOutTheme.colorScheme.accent.color,
                fontWeight = FontWeight.ExtraBold,
                textDecoration = TextDecoration.Underline
            )
        )

    ClickableText(
        modifier = modifier.fillMaxWidth(),
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = termsOfServiceText,
                start = offset,
                end = offset
            )
                .firstOrNull()
                ?.let {
                    onTermsOfServiceClicked()
                }

            annotatedString.getStringAnnotations(
                tag = privacyPolicyText,
                start = offset,
                end = offset
            )
                .firstOrNull()
                ?.let {
                    onPrivacyPolicyClicked()
                }
        },
        style = OddOneOutTheme.typography.Body.B700.style.copy(color = OddOneOutTheme.colorScheme.text.color),
    )
}

@Preview
@Composable
private fun PreviewBlockingErrorScreen() {
    PreviewContent {
        InHouseConsentScreen(
            onAcceptClicked = { -> },
            onTermsOfServiceClicked = { -> },
            onPrivacyPolicyClicked = { -> }
        )
    }
}
