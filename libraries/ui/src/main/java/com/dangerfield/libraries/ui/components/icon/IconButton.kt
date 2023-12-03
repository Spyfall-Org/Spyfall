package com.dangerfield.libraries.ui.components.icon

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.tooltip.TooltipBox
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import spyfallx.ui.Spacing
import spyfallx.ui.color.ColorToken
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@NonRestartableComposable
@Composable
fun IconButton(
    icon: SpyfallIcon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    backgroundColor: ColorToken.Color? = null,
    iconColor: ColorToken.Color = SpyfallTheme.colorScheme.onBackground,
    size: IconButton.Size = IconButton.Size.Medium,
    enabled: Boolean = true,
    tooltipText: String = icon.contentDescription,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    TooltipBox(tooltip = { Text(text = tooltipText) }) {
        val padding = size.padding
        val iconSize = size.iconSize
        Surface(
            modifier = modifier.tooltipAnchor(),
            contentPadding = PaddingValues(padding),
            color = backgroundColor,
            contentColor = iconColor,
            radius = Radii.IconButton,
            onClick = onClick,
            enabled = enabled,
            role = Role.Button,
            interactionSource = interactionSource
        ) {
            Icon(
                spyfallIcon = icon,
                iconSize = iconSize
            )
        }
    }
}

object IconButton {
    enum class Size {
        Small,
        Medium,
        Large,
    }
}

internal val IconButton.Size.padding: Dp
    get() = when (this) {
        IconButton.Size.Small -> Spacing.S100
        IconButton.Size.Medium -> Spacing.S100
        IconButton.Size.Large -> Spacing.S200
    }

internal val IconButton.Size.iconSize: IconSize
    get() = when (this) {
        IconButton.Size.Small -> IconSize.Small
        IconButton.Size.Medium -> IconSize.Medium
        IconButton.Size.Large -> IconSize.Large
    }

private val iconButtons = listOf(
    SpyfallIcon.Add(""),
    SpyfallIcon.Bookmark(""),
    SpyfallIcon.Info(""),
    SpyfallIcon.Check(""),
    SpyfallIcon.Close(""),
    SpyfallIcon.MoreVert(""),
    SpyfallIcon.Person(""),
    SpyfallIcon.Settings(""),
)

@Suppress("MagicNumber")
@Preview(device = "spec:shape=Normal,width=1200,height=400,unit=dp,dpi=150")
@Composable
private fun PreviewIconButtons() {
    PreviewContent(showBackground = true) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(iconButtons) { icon ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    IconButton(
                        icon = icon,
                        modifier = Modifier.size(48.dp),
                        backgroundColor = null,
                        size = IconButton.Size.Medium,
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = icon::class.java.simpleName)
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Preview(device = "spec:shape=Normal,width=1200,height=400,unit=dp,dpi=150")
@Composable
private fun PreviewIconButtonsBackground() {
    PreviewContent(showBackground = true, isDarkMode = true) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(iconButtons) { icon ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    IconButton(
                        icon = icon,
                        modifier = Modifier.size(48.dp),
                        backgroundColor = SpyfallTheme.colorScheme.onBackground,
                        iconColor = SpyfallTheme.colorScheme.background,
                        size = IconButton.Size.Medium,
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = icon::class.java.simpleName)
                }
            }
        }
    }
}