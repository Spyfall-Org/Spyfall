package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
                title = "About",
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
                text = "Privacy Policy",
                onClick = onPrivacyPolicyClicked,
                trailingIcon = SpyfallIcon.ChevronRight("About Spyfall"),
            )

            SettingsOption(
                text = "Terms of Service",
                onClick = onTermsOfServiceClicked,
                trailingIcon = SpyfallIcon.ChevronRight("About Spyfall"),
            )

            SettingsOption(
                text = "Third Party Services",
                onClick = onThirdPartyServicesClicked,
                trailingIcon = SpyfallIcon.ChevronRight("About Spyfall"),
            )

            Text(
                text = stringResource(id = R.string.app_name)+ " Version: $versionName",
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
