package com.dangerfield.features.qa.internal.item

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.DropdownMenuItem
import com.dangerfield.libraries.ui.components.ListItem
import com.dangerfield.libraries.ui.components.RoundedDropdownMenu
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun QaDropDownItem(
    modifier: Modifier = Modifier,
    supportingText: (@Composable () -> Unit)? = null,
    menuItems: List<MenuItem> = emptyList(),
    isDebug: Boolean = false,
    content: @Composable () -> Unit,
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier,
        trailingContent = {
            Box {
                IconButton(
                    icon = SpyfallIcon.DropDown("Navigate to option"),
                    size = IconButton.Size.Large,
                    onClick = { isMenuOpen = true },
                )
                RoundedDropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = { isMenuOpen = false }) {
                    menuItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.text) },
                            onClick = {
                                item.onClick?.invoke()
                                isMenuOpen = false
                            }
                        )
                    }
                }
            }
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
        headlineContent = content
    )
}

data class MenuItem(val text: String, val onClick: (() -> Unit)? = null)

@Preview
@Composable
private fun PreviewQaDropDownItem() {
    PreviewContent {
        QaDropDownItem(
            menuItems = listOf(
                MenuItem("Item 1"),
                MenuItem("Item 2"),
                MenuItem("Item 3"),
            )
        ) {
            Text("Title")
        }
    }
}


