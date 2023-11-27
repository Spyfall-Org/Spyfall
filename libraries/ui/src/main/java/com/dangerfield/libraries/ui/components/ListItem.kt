package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.icon.Icon
import com.dangerfield.libraries.ui.icon.SpyfallIcon
import spyfallx.ui.Spacing
import spyfallx.ui.theme.SpyfallTheme

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit = {},
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    headlineContent: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickItem() }
            .padding(vertical = Spacing.S500),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent?.let {
            it()
            Spacer(modifier = Modifier.width(Spacing.S500))
        }
        Column {
            headlineContent()
            ProvideTextConfig(
                typographyToken = SpyfallTheme.typography.Body.B600
            ) {
                supportingContent?.let { it() }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        trailingContent?.let { it() }
    }
}

@Composable
@ThemePreviews
fun PreviewSettingsOption() {
    PreviewContent(showBackground = true) {
        ListItem(
            headlineContent =
            {
                Text(text = "Headline")
            },
            supportingContent = {
                Text(text = "Supporting")
            },

            leadingContent = {
                Icon(SpyfallIcon.Android("Android"))
            },

            trailingContent = {
                Icon(SpyfallIcon.Android("Android"))
            }
        )
    }
}

