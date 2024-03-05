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
import androidx.compose.runtime.Stable
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
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

@Suppress("ClassNaming")
@Stable
sealed class ColorResource(val color: Color, val designSystemName: String) {
    object Unspecified : ColorResource(Color.Unspecified, "unspecified")
    object Black900 : ColorResource(Color(0xFF000000), "black-900")
    object Black900_A_70 :
        ColorResource(Color(0xFF000000).copy(alpha = 0.7f), "black-900-a-70")

    object Black800 : ColorResource(Color(0xFF1A1A1A), "black-800")
    object Black700 : ColorResource(Color(0xFF313131), "black-700")
    object Black600 : ColorResource(Color(0xFF767676), "black-600")
    object Black500 : ColorResource(Color(0xFFAAAAAA), "black-500")
    object Black400 : ColorResource(Color(0xFFCCCCCC), "black-400")
    object Black300 : ColorResource(Color(0xFFEEEEEE), "black-300")
    object Black200 : ColorResource(Color(0xFFF6F6F6), "black-200")
    object Black100 : ColorResource(Color(0xFFFBFBFB), "black-100")
    object Purple900 : ColorResource(Color(0xFF270B68), "purple-900")
    object Purple700 : ColorResource(Color(0xFF422091), "purple-700")
    object Purple500 : ColorResource(Color(0xFF4C27A3), "purple-500")
    object Purple400 : ColorResource(Color(0xFF5B35B4), "purple-400")
    object Purple300 : ColorResource(Color(0xFF784CDF), "purple-300")
    object Purple100 : ColorResource(Color(0xFFA179FF), "purple-100")
    object PurpleGray300 : ColorResource(Color(0xFF8C79B8), "purple-300")
    object White900 : ColorResource(Color(0xFFFFFFFF), "white-900")
    object GrapeJelly500 : ColorResource(Color(0xFF9533C7), "grape-jelly-500")
    object SkyDive400 : ColorResource(Color(0xFF00A0EF), "sky-dive-400")
    object MintyFresh300 : ColorResource(Color(0xFF2FD566), "minty-fresh-300")
    object TangerineTwist600 : ColorResource(Color(0xFFFF5800), "tangerine-twist-600")
    object CherryPop700 : ColorResource(Color(0xFFE3212F), "cherry-pop-700")
    internal class FromColor(color: Color, name: String) : ColorResource(color, name)

    val onColorResource: ColorResource
        get() {
            return if (color.luminance() > 0.4) Black900 else White900
        }
}

@Composable
fun animateColorResourceAsState(
    targetValue: ColorResource,
    animationSpec: AnimationSpec<ColorResource> = spring(),
    label: String = "ColorAnimation",
    finishedListener: ((ColorResource) -> Unit)? = null,
): State<ColorResource> {
    val converter: TwoWayConverter<ColorResource, AnimationVector4D> = remember(targetValue) {
        val colorConverter = (Color.VectorConverter)(targetValue.color.colorSpace)
        TwoWayConverter(convertToVector = { token: ColorResource ->
            colorConverter.convertToVector(token.color)
        }, convertFromVector = { vector ->
            ColorResource.FromColor(
                color = colorConverter.convertFromVector(vector),
                name = targetValue.designSystemName
            )
        })
    }

    return animateValueAsState(
        targetValue = targetValue,
        typeConverter = converter,
        animationSpec = animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}

private val colors = listOf(
    ColorResource.Black900,
    ColorResource.Black900_A_70,
    ColorResource.Black800,
    ColorResource.Black700,
    ColorResource.Black600,
    ColorResource.Black500,
    ColorResource.Black400,
    ColorResource.Black300,
    ColorResource.Black200,
    ColorResource.Black100,
    ColorResource.Purple900,
    ColorResource.Purple700,
    ColorResource.Purple500,
    ColorResource.Purple400,
    ColorResource.Purple300,
    ColorResource.Purple100,
    ColorResource.PurpleGray300,
    ColorResource.White900,
    ColorResource.GrapeJelly500,
    ColorResource.SkyDive400,
    ColorResource.MintyFresh300,
    ColorResource.TangerineTwist600,
    ColorResource.CherryPop700,
)

@Preview(widthDp = 400, heightDp = 1500, showBackground = false)
@Composable
private fun PreviewColorSwatch() {
    Preview(showBackground = false) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3)
        ) {
            items(colors) { colorResource ->
                ColorCard(colorResource)
            }
        }
    }
}


@Composable
internal fun ColorCard(colorResource: ColorResource) {
    Box(
        modifier = Modifier
            .padding(Dimension.D100)
            .background(colorResource.color, shape = Radii.Card.shape)
            .height(150.dp)
            .width(120.dp)
            .clip(Radii.Card.shape)
    ) {
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
