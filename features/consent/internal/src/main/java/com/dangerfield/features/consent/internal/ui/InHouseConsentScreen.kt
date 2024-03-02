package com.dangerfield.features.consent.internal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.HorizontalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.checkbox.Checkbox
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.makeClickable
import com.dangerfield.libraries.ui.Preview
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
                .padding(horizontal = Dimension.D1100),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isChecked: Boolean by remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current

            VerticalSpacerD800()

            Text(
                text = dictionaryString(id = R.string.termsOfService_screen_header),
                typography = OddOneOutTheme.typography.Display.D1000
            )

            VerticalSpacerD1200()

            Text(
                text = dictionaryString(R.string.termsOfService_description_text),
                typography = OddOneOutTheme.typography.Body.B700
            )

            VerticalSpacerD800()

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
                        isChecked = !isChecked
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    modifier = Modifier.padding(end = Dimension.D400)
                )
                HorizontalSpacerD500()
                Text(
                    text = dictionaryString(R.string.termsOfService_confirmation_text),
                    typography = OddOneOutTheme.typography.Body.B600
                )
            }

            VerticalSpacerD1200()

            Button(
                style = ButtonStyle.Background,
                enabled = isChecked,
                onClick = {
                    focusManager.clearFocus()
                    if (isChecked) {
                        onAcceptClicked()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(R.string.termsOfService_accept_action))
            }

            VerticalSpacerD1200()
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
        .makeClickable(
            linkText = termsOfServiceText,
            annotation = termsOfServiceText to "link",
            style = SpanStyle(
                color = OddOneOutTheme.colors.accent.color,
                fontWeight = FontWeight.ExtraBold,
                textDecoration = TextDecoration.Underline
            )
        )
        .makeClickable(
            linkText = privacyPolicyText,
            annotation = privacyPolicyText to "link",
            style = SpanStyle(
                color = OddOneOutTheme.colors.accent.color,
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
        style = OddOneOutTheme.typography.Body.B700.style.copy(color = OddOneOutTheme.colors.text.color),
    )
}

@Preview
@Composable
private fun PreviewBlockingErrorScreen() {
    Preview {
        InHouseConsentScreen(
            onAcceptClicked = { -> },
            onTermsOfServiceClicked = { -> },
            onPrivacyPolicyClicked = { -> }
        )
    }
}
