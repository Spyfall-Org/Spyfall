package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.settings.internal.R

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    versionName: String,
    isQaOptionEnabled: Boolean = false,
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
                title = dictionaryString(R.string.settings_settingsScreen_header),
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Dimension.D800)
        ) {

            SettingsOption(
                text = dictionaryString(R.string.settings_theme_label),
                onClick = onThemeOptionClicked,
                leadingIcon = SpyfallIcon.Theme(null),
            )

            SettingsOption(
                leadingIcon = SpyfallIcon.Info(null),
                text = dictionaryString(R.string.settings_about_label),
                onClick = onAboutOptionClicked
            )

            SettingsOption(
                text = dictionaryString(R.string.settings_contactUs_label),
                onClick = onContactUsClicked,
                leadingIcon = SpyfallIcon.Chat(null),
            )

            SettingsOption(
                text = dictionaryString(R.string.settings_stats_label),
                onClick = onStatsClicked,
                leadingIcon = SpyfallIcon.Graph(null),
            )

            if (isQaOptionEnabled) {
                SettingsOption(
                    text = dictionaryString(R.string.settings_qaMenu_label),
                    onClick = onQaOptionClicked,
                    leadingIcon = SpyfallIcon.Android(null),
                )
            }

            VerticalSpacerD1200()

            Text(
                text = dictionaryString(R.string.settings_madeWithLove_text),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                typographyToken = OddOneOutTheme.typography.Body.B700,
                colorResource = OddOneOutTheme.colors.textDisabled
            )

            Text(
                text = dictionaryString(id = R.string.app_name_text) + dictionaryString(
                    R.string.settings_version_label,
                    "version" to versionName
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                typographyToken = OddOneOutTheme.typography.Body.B700,
                colorResource = OddOneOutTheme.colors.textDisabled
            )
        }
    }
}

@Composable
@Preview
private fun PreviewSettingsScreen() {
    Preview {
        SettingsScreen(
            versionName = "X.Y.Z",
            isQaOptionEnabled = true
        )
    }
}
