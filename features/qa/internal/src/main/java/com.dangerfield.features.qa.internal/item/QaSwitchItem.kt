package com.dangerfield.features.qa.internal.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.components.ListItem
import com.dangerfield.libraries.ui.components.Switch
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun QASwitchItem(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isDebug: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
    headlineContent: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    ListItem(
        modifier = modifier.clickable(
            role = Role.Checkbox,
            enabled = enabled,
            indication = null,
            interactionSource = interactionSource,
            onClick = onClick ?: { onCheckedChanged(!checked) }
        ),
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChanged,
                enabled = enabled,
                interactionSource = interactionSource
            )
        },
        leadingContent = {
            if (isDebug) {
                Icon(spyfallIcon = SpyfallIcon.Bug(""))
            }
        },
        supportingContent = {
            ProvideTextConfig(typographyToken = OddOneOutTheme.typography.Body.B600) {
                supportingText?.invoke()
            }
        },
        headlineContent = headlineContent
    )
}

@Preview
@Composable
private fun QAItem() {
    PreviewContent {
        var checked by rememberSaveable { mutableStateOf(false) }
        QASwitchItem(
            checked = checked,
            onCheckedChanged = { checked = it },
            supportingText = { Text("Supporting text") }
        ) {
            Text("Check me")
        }
    }
}
