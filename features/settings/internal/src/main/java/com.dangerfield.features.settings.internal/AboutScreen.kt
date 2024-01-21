package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.settings.internal.R

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    versionName: String,
    onPrivacyPolicyClicked: () -> Unit = { },
    onTermsOfServiceClicked: () -> Unit = { },
    onThirdPartyServicesClicked: () -> Unit = { },
    onNavigateBack: () -> Unit = { },
) {
    Screen(
        modifier = modifier,
        topBar = {
            Header(
                title = dictionaryString(R.string.settings_about_header),
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Spacing.S800)
        ) {

            SettingsOption(
                text = dictionaryString(R.string.about_privacyPolicy_label),
                onClick = onPrivacyPolicyClicked,
                trailingIcon = SpyfallIcon.ChevronRight(null),
            )

            SettingsOption(
                text = dictionaryString(R.string.about_termsOfService_label),
                onClick = onTermsOfServiceClicked,
                trailingIcon = SpyfallIcon.ChevronRight(null),
            )

            SettingsOption(
                text = dictionaryString(R.string.about_thirdPartyServices_label),
                onClick = onThirdPartyServicesClicked,
                trailingIcon = SpyfallIcon.ChevronRight(null),
            )

            Text(
                text = dictionaryString(id = R.string.app_name_text) + dictionaryString(
                    R.string.settings_version_label,
                    mapOf("version" to versionName)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.S1000),
                textAlign = TextAlign.Center,
                typographyToken = OddOneOutTheme.typography.Body.B700,
                color = OddOneOutTheme.colorScheme.textDisabled
            )
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewSettingsScreen() {
    PreviewContent {
        AboutScreen(
            versionName = "X.Y.Z",
            onPrivacyPolicyClicked = { -> },
            onTermsOfServiceClicked = { -> },
            onThirdPartyServicesClicked = { -> },
            onNavigateBack = { -> },
        )
    }
}
