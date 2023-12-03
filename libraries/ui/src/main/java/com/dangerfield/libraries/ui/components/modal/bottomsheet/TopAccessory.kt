package com.dangerfield.libraries.ui.components.modal.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Immutable
sealed class TopAccessory {
    @Immutable
    data object DragHandle : TopAccessory()

    @Immutable
    data class Icon(
        val icon: SpyfallIcon,
        val color: Color,
        val backgroundColor: Color,
        val iconSize: IconSize = IconSize.Small
    ) : TopAccessory()
}


@Composable
fun dragHandleTopAccessory() = TopAccessory.DragHandle

@Composable
fun iconTopAccessory(
    icon: SpyfallIcon,
    color: Color = SpyfallTheme.colorScheme.onBackground.color,
    backgroundColor: Color = SpyfallTheme.colorScheme.background.color,
    iconSize: IconSize = IconSize.Small
) = TopAccessory.Icon(icon, color, backgroundColor, iconSize)