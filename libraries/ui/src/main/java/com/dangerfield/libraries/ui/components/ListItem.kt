package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

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
        Column(modifier = Modifier.weight(1f)) {
            headlineContent()
            ProvideTextConfig(
                typographyToken = OddOneOutTheme.typography.Body.B600
            ) {
                supportingContent?.let { it() }
            }
        }
        Spacer(modifier = Modifier.width(Spacing.S500))
        trailingContent?.let {
            Box {
                it()
            }
        }
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
