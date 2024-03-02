package com.dangerfield.libraries.ui.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import com.dangerfield.libraries.ui.LocalContentColor

@Immutable
@Suppress("LongParameterList")
class Colors internal constructor(

    val accent: ColorResource,
    val onAccent: ColorResource,

    /* Backgrounds */
    val shadow: ColorResource,
    val background: ColorResource,
    val backgroundOverlay: ColorResource,
    val onBackground: ColorResource,
    val border: ColorResource,
    val borderDisabled: ColorResource,

    /* Texts */
    val text: ColorResource,
    val textDisabled: ColorResource,
    val textWarning: ColorResource,

    /* Surfaces */
    val surfacePrimary: ColorResource,
    val onSurfacePrimary: ColorResource,
    val surfaceSecondary: ColorResource,
    val onSurfaceSecondary: ColorResource,
    val surfaceDisabled: ColorResource,
    val onSurfaceDisabled: ColorResource,

    ) {
    internal companion object {
        fun getColors(accentColor: ColorResource = ColorResource.CherryPop700) = Colors(
            accent = accentColor,
            onAccent = accentColor.onColorResource,
            shadow = ColorResource.Black800,
            textDisabled = ColorResource.PurpleGray300,
            textWarning = ColorResource.CherryPop700,
            surfacePrimary = ColorResource.Purple500,
            onSurfacePrimary = ColorResource.White900,
            surfaceSecondary = ColorResource.Purple700,
            onSurfaceSecondary = ColorResource.White900,
            surfaceDisabled = ColorResource.Black400,
            onSurfaceDisabled = ColorResource.Black600,
            background = ColorResource.Purple900,
            onBackground = ColorResource.White900,
            border = ColorResource.White900,
            text = ColorResource.White900,
            backgroundOverlay = ColorResource.Black900_A_70,
            borderDisabled = ColorResource.Black300,
            )
    }
}

@Composable
fun ProvideContentColor(color: ColorResource, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentColor provides color,
        androidx.compose.material3.LocalContentColor provides color.color,
        content = content
    )
}

