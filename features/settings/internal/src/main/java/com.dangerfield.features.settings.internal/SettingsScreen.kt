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
import com.dangerfield.spyfall.libraries.resources.R
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    versionName: String,
    isDebug: Boolean = false,
    onThemeOptionClicked: () -> Unit = { },
    onQaOptionClicked: () -> Unit = { },
    onStatsClicked: () -> Unit = { },
    onAboutOptionClicked: () -> Unit = { },
    onNavigateBack: () -> Unit = { },
    onContactUsClicked: () -> Unit = { },
) {
    Screen(
        modifier = modifier,
        topBar = {
            Header(
                title = "Settings",
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
                text = "Theme",
                onClick = onThemeOptionClicked,
                leadingIcon = SpyfallIcon.Theme("Change Theme"),
            )

            SettingsOption(
                leadingIcon = SpyfallIcon.Info("About"),
                text = "About",
                onClick = onAboutOptionClicked
            )

            SettingsOption(
                text = "Contact Us",
                onClick = onContactUsClicked,
                leadingIcon = SpyfallIcon.Chat("Feedback"),
            )

            SettingsOption(
                text = "Stats",
                onClick = onStatsClicked,
                leadingIcon = SpyfallIcon.Graph("Stats"),
            )

            if (isDebug) {
                SettingsOption(
                    text = "QA Menu",
                    onClick = onQaOptionClicked,
                    leadingIcon = SpyfallIcon.Android("Feedback"),
                )
            }

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
        SettingsScreen(
            versionName = "X.Y.Z",
            isDebug = true
        )
    }
}
