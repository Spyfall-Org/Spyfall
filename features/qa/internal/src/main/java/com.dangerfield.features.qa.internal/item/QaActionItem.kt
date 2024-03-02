package com.dangerfield.features.qa.internal.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.ListItem
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun QaActionItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    actionText: String,
    enabled: Boolean = true,
    supportingText: (@Composable () -> Unit)? = null,
    headline: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    ListItem(
        modifier = modifier.clickable(
            enabled = enabled,
            indication = null,
            interactionSource = interactionSource,
            onClick = onClick ?: {}
        ),
        trailingContent = {
            Button(
                size = ButtonSize.ExtraSmall,
                onClick = { onClick?.invoke() }
            ) {
                Text(text = actionText)
            }
        },
        supportingContent = {
            ProvideTextConfig(typography = OddOneOutTheme.typography.Body.B600) {
                supportingText?.invoke()
            }
        },
        headlineContent = {
            ProvideTextConfig(typography = OddOneOutTheme.typography.Heading.H700) {
                headline.invoke()
            }
        }
    )
}

@Preview
@Composable
private fun PreviewQaActionItem() {
    Preview {
        QaActionItem(
            onClick = { -> },
            actionText = "Reset",
            supportingText = {
                Text(text = "resets something to default value")
            },
            headline = {
                Text(text = "Reset something")
            }
        )
    }
}


