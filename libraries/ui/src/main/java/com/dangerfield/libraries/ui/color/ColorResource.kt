package com.dangerfield.libraries.ui.color

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD100
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Suppress("ClassNaming")
sealed class ColorResource(val color: Color, val designSystemName: String) {
    data object Unspecified: ColorResource(Color.Unspecified, "color-unspecified")
    data object Black900: ColorResource(Color(0xFF000000), "color-black-900")
    data object Black900_A_70: ColorResource(Color(0xFF000000).copy(alpha = 0.7f), "color-black-900-a-70")
    data object Black800: ColorResource(Color(0xFF1A1A1A), "color-black-800")
    data object Black700: ColorResource(Color(0xFF313131), "color-black-700")
    data object Black600: ColorResource(Color(0xFF767676), "color-black-600")
    data object Black500: ColorResource(Color(0xFFAAAAAA), "color-black-500")
    data object Black400: ColorResource(Color(0xFFCCCCCC), "color-black-400")
    data object Black300: ColorResource(Color(0xFFEEEEEE), "color-black-300")
    data object Black200: ColorResource(Color(0xFFF6F6F6), "color-black-200")
    data object Black100: ColorResource(Color(0xFFFBFBFB),  "color-black-100")
    data object Purple900: ColorResource(Color(0xFF270B68), "color-purple-900")
    data object Purple700: ColorResource(Color(0xFF422091), "color-purple-700")
    data object Purple500: ColorResource(Color(0xFF4C27A3), "color-purple-500")
    data object Purple400: ColorResource(Color(0xFF5B35B4), "color-purple-400")
    data object Purple300: ColorResource(Color(0xFF784CDF), "color-purple-300")
    data object Purple100: ColorResource(Color(0xFFA179FF), "color-purple-100")
    data object PurpleGray300: ColorResource(Color(0xFF8C79B8), "color-purple-300")
    data object White900: ColorResource(Color(0xFFFFFFFF), "color-white-900")
    data object GrapeJelly500: ColorResource(Color(0xFF9533C7), "color-grape-jelly-500")
    data object SkyDive400: ColorResource(Color(0xFF00A0EF), "color-sky-dive-400")
    data object MintyFresh300: ColorResource(Color(0xFF2FD566), "color-minty-fresh-300")
    data object TangerineTwist600: ColorResource(Color(0xFFFF5800), "color-tangerine-twist-600")
    data object CherryPop700: ColorResource(Color(0xFFE3212F), "color-cherry-pop-700")
    internal class FromColor(color: Color, name: String): ColorResource(color, name)

    fun copyWith(alpha: Float): ColorResource {
        return FromColor(color.copy(alpha = alpha), this.designSystemName)
    }

    val onColorResource: ColorResource
        get() {
            return if (color.luminance() > 0.4) Black900 else White900
        }

}
val ColorResource.name: String
    get() = this.toString().lowercase()
        .replace(Regex("""([A-Za-z]+)(\d+)"""), """color-$1-$2""")

sealed class ColorGradientResource(val from: ColorResource, val to: ColorResource) {
    object Dark: ColorGradientResource(ColorResource.Black900, ColorResource.Black800)
    object Light: ColorGradientResource(ColorResource.Black100, ColorResource.Black200)
    class FromTo(from: ColorResource, to: ColorResource): ColorGradientResource(from, to)
    val colorOn: Color = from.onColorResource.color
}

val ColorGradientResource.name: String
    get() = "color-gradient-${this.javaClass.simpleName.lowercase()}"

@Composable
fun animateColorResourceAsState(
    targetValue: ColorResource,
    animationSpec: AnimationSpec<ColorResource> = spring(),
    label: String = "ColorAnimation",
    finishedListener: ((ColorResource) -> Unit)? = null,
): State<ColorResource> {
    val converter: TwoWayConverter<ColorResource, AnimationVector4D> = remember(targetValue) {
        val colorConverter = (Color.VectorConverter)(targetValue.color.colorSpace)
        TwoWayConverter(
            convertToVector = { token: ColorResource ->
                colorConverter.convertToVector(token.color)
            },
            convertFromVector = { vector ->
                ColorResource.FromColor(
                    color = colorConverter.convertFromVector(vector),
                    name = targetValue.designSystemName
                )
            }
        )
    }

    return animateValueAsState(
        targetValue = targetValue,
        typeConverter = converter,
        animationSpec = animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}


@Preview(widthDp = 360, heightDp = 640, showBackground = true)
@Composable
private fun PreviewColorSwatch() {
    val colorResources = remember {
        sealedValues<ColorResource>()
    }

    Preview {

        Text(text = "Number: ${colorResources.size}")
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(colorResources) { colorResource ->
                ColorCard(colorResource)
            }
        }
    }
}


@Composable
internal fun ColorCard(colorResource: ColorResource) {
    Box(modifier = Modifier
        .background(colorResource.color, shape = Radii.Card.shape)
        .height(150.dp)
        .width(120.dp)
        .clip(Radii.Card.shape)
    )

    {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.BottomCenter)
                .padding(Dimension.D500)
        ) {

            Text(
                text = colorResource.designSystemName,
                colorResource = ColorResource.Black900,
                typography = OddOneOutTheme.typography.Label.L400.Bold,
            )

            VerticalSpacerD100()

            Text(
                text = colorResource.toHexString(),
                colorResource = ColorResource.Black600,
                typography = OddOneOutTheme.typography.Label.L400.SemiBold,
            )
        }

    }
}

/**
 * Extension function to convert a Color object to a hexadecimal string representation.
 * Includes the alpha value by default but can be omitted.
 *
 * @param includeAlpha whether to include the alpha value in the hex string.
 * @return A hex string representation of the color (e.g., "#FFFFFFFF" or "#FFFFFF" if alpha is omitted).
 */
internal fun ColorResource.toHexString(includeAlpha: Boolean = true): String {
    val color = this.color
    val alpha = if (includeAlpha) "%02X".format((color.alpha * 255).toInt()) else ""
    val red = "%02X".format((color.red * 255).toInt())
    val green = "%02X".format((color.green * 255).toInt())
    val blue = "%02X".format((color.blue * 255).toInt())
    return "#$alpha$red$green$blue"
}

inline fun <reified T> sealedValues(): List<T> {
    return T::class.sealedSubclasses.mapNotNull { it.objectInstance as T }
}

