package com.dangerfield.libraries.ui.color

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

sealed class ColorResource(val color: Color) {
    data object Unspecified: ColorResource(Color.Unspecified)
    data object Black900: ColorResource(Color(0xFF000000))
    data object Black800: ColorResource(Color(0xFF1A1A1A))
    data object Black700: ColorResource(Color(0xFF313131))
    data object Black600: ColorResource(Color(0xFF767676))
    data object Black500: ColorResource(Color(0xFFAAAAAA))
    data object Black400: ColorResource(Color(0xFFCCCCCC))
    data object Black300: ColorResource(Color(0xFFEEEEEE))
    data object Black200: ColorResource(Color(0xFFF6F6F6))
    data object Black100: ColorResource(Color(0xFFFBFBFB))
    data object Purple900: ColorResource(Color(0xFF270B68))
    data object Purple700: ColorResource(Color(0xFF422091))
    data object Purple500: ColorResource(Color(0xFF4C27A3))
    data object Purple400: ColorResource(Color(0xFF5B35B4))
    data object Purple300: ColorResource(Color(0xFF784CDF))
    data object Purple100: ColorResource(Color(0xFFA179FF))
    data object White900: ColorResource(Color(0xFFFFFFFF))
    data object GrapeJelly500: ColorResource(Color(0xFF9533C7))
    data object SkyDive400: ColorResource(Color(0xFF00A0EF))
    data object MintyFresh300: ColorResource(Color(0xFF2FD566))
    data object TangerineTwist600: ColorResource(Color(0xFFFF5800))
    data object CherryPop700: ColorResource(Color(0xFFE3212F))
    class FromColor(color: Color): ColorResource(color)

    fun copyWith(alpha: Float): ColorResource {
        return FromColor(color.copy(alpha = alpha))
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
    object Dark: ColorGradientResource(ColorResource.FromColor(Color.Black), ColorResource.Black800)
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
                    color = colorConverter.convertFromVector(vector)
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