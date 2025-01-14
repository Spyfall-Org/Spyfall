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
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@NonRestartableComposable
@Composable
fun IconButton(
    icon: SpyfallIcon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    backgroundColor: ColorResource? = null,
    iconColor: ColorResource = OddOneOutTheme.colors.onBackground,
    size: IconButton.Size = IconButton.Size.Medium,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val padding = size.padding
    val iconSize = size.iconSize

    @Composable
    fun Button(modifier: Modifier) {
        Surface(
            modifier = modifier,
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

    Button(modifier = modifier)

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
        IconButton.Size.Small -> Dimension.D100
        IconButton.Size.Medium -> Dimension.D100
        IconButton.Size.Large -> Dimension.D200
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
    SpyfallIcon.Share(""),
    )

@Suppress("MagicNumber")
@Preview(device = "spec:shape=Normal,width=1200,height=400,unit=dp,dpi=150")
@Composable
private fun PreviewIconButtons() {
    Preview(showBackground = true) {
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
    Preview(showBackground = true) {
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
                        backgroundColor = OddOneOutTheme.colors.onBackground,
                        iconColor = OddOneOutTheme.colors.background,
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