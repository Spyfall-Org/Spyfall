package com.dangerfield.libraries.ui.components.modal.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

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
    color: Color = OddOneOutTheme.colorScheme.background.color,
    backgroundColor: Color = OddOneOutTheme.colorScheme.onBackground.color,
    iconSize: IconSize = IconSize.Small
) = TopAccessory.Icon(icon, color, backgroundColor, iconSize)