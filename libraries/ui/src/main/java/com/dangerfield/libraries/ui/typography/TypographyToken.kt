@file:Suppress("MagicNumber", "VariableNaming")
package com.dangerfield.libraries.ui.typography

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import spyfallx.ui.NumericalValues
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Radii
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.ui.typography.FontSize
import kotlin.math.roundToInt

@Suppress("ComplexMethod")
data class TypographyToken internal constructor(
    internal val fontFamily: FontFamilyToken,
    internal val fontWeight: FontWeight,
    internal val fontSize: TextUnit,
    internal val lineHeight: TextUnit,
    internal val lineBreak: LineBreak,
    internal val fontStyle: FontStyle = FontStyle.Normal,
    internal val identifier: String
) {

    val style: TextStyle = TextStyle(
        fontFamily = fontFamily.fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontStyle = fontStyle,
        lineBreak = lineBreak,
    )

    fun style(color: Color) = TextStyle(
        fontFamily = fontFamily.fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontStyle = fontStyle,
        lineBreak = lineBreak,
        color = color
    )

    val Italic: TypographyToken
        get() = TypographyToken(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            lineHeight = lineHeight,
            lineBreak = lineBreak,
            fontStyle = FontStyle.Italic,
            identifier = "${identifier}-italic"
        )

    val Bold: TypographyToken
        get() = TypographyToken(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            lineHeight = lineHeight,
            lineBreak = lineBreak,
            fontStyle = fontStyle,
            identifier = "${identifier}-bold"
        )

    val SemiBold: TypographyToken
        get() = TypographyToken(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
            lineHeight = lineHeight,
            lineBreak = lineBreak,
            fontStyle = fontStyle,
            identifier = "${identifier}-semibold"
        )
}

class Typography {
    val Display = DisplayTypography()
    val Heading = HeadingTypography()
    val Body = BodyTypography()
    val Label = LabelTypography()

    val Default = Heading.H800

    class DisplayTypography {

        val D1500 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1500,
            lineHeight = LineHeight.H1500,
            lineBreak = LineBreak.Heading,
            identifier = "display-1500"
        )

        val D1400 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1400,
            lineHeight = LineHeight.H1400,
            lineBreak = LineBreak.Heading,
            identifier = "display-1400"
        )

        val D1300 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1300,
            lineHeight = LineHeight.H1200,
            lineBreak = LineBreak.Heading,
            identifier = "display-1300"
        )

        val D1200 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1200,
            lineHeight = LineHeight.H1200,
            lineBreak = LineBreak.Heading,
            identifier = "display-1100"
        )


        val D1100 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1100,
            lineHeight = LineHeight.H1200,
            lineBreak = LineBreak.Heading,
            identifier = "display-1100"
        )

        val D1000 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1000,
            lineHeight = LineHeight.H1100,
            lineBreak = LineBreak.Heading,
            identifier = "display-1000"
        )

        val D900 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S900,
            lineHeight = LineHeight.H1000,
            lineBreak = LineBreak.Heading,
            identifier = "display-900"
        )

        val D800 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S800,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Heading,
            identifier = "display-800"
        )
    }

    class HeadingTypography {

        val H1200 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S1200,
            lineHeight = LineHeight.H1200,
            lineBreak = LineBreak.Heading,
            identifier = "heading-1200"
        )

        val H1100 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S1100,
            lineHeight = LineHeight.H1100,
            lineBreak = LineBreak.Heading,
            identifier = "heading-1100"
        )

        val H1000 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S1000,
            lineHeight = LineHeight.H1000,
            lineBreak = LineBreak.Heading,
            identifier = "heading-1000"
        )

        val H900 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S900,
            lineHeight = LineHeight.H1000,
            lineBreak = LineBreak.Heading,
            identifier = "heading-900"
        )

        val H800 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S800,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Heading,
            identifier = "heading-800"
        )

        val H700 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S700,
            lineHeight = LineHeight.H800,
            lineBreak = LineBreak.Heading,
            identifier = "heading-700"
        )

        val H600 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S600,
            lineHeight = LineHeight.H700,
            lineBreak = LineBreak.Heading,
            identifier = "heading-600"
        )

        val H500 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S500,
            lineHeight = LineHeight.H700,
            lineBreak = LineBreak.Heading,
            identifier = "heading-500"
        )

        val H400 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S400,
            lineHeight = LineHeight.H700,
            lineBreak = LineBreak.Heading,
            identifier = "heading-400"
        )
    }

    class LabelTypography {

        val L800 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S800,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Paragraph,
            identifier = "label-800"
        )

        val L700 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S700,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Paragraph,
            identifier = "label-700"
        )

        val L600 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S600,
            lineHeight = LineHeight.H600,
            lineBreak = LineBreak.Paragraph,
            identifier = "label-600"
        )

        val L500 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S500,
            lineHeight = LineHeight.H500,
            lineBreak = LineBreak.Paragraph,
            identifier = "label-500"
        )

        val L400 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S400,
            lineHeight = LineHeight.H500,
            lineBreak = LineBreak.Paragraph,
            identifier = "label-400"
        )
    }

    class BodyTypography {

        val B800 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S800,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Paragraph,
            identifier = "body-800"
        )

        val B700 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S700,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Paragraph,
            identifier = "body-700"
        )


        val B600 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S600,
            lineHeight = LineHeight.H800,
            lineBreak = LineBreak.Paragraph,
            identifier = "body-600"
        )

        val B500 = TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S500,
            lineHeight = LineHeight.H700,
            lineBreak = LineBreak.Paragraph,
            identifier = "body-500"
        )
    }
}

@Suppress("ComplexMethod")
@Preview(device = "spec:id=reference_phone,shape=Normal,width=1200,height=2000,unit=dp,dpi=200")
@Composable
private fun TokensPreview(
    @PreviewParameter(PreviewGroupPreviewParameterProvider::class) group: PreviewGroup,
) {
    PreviewContent(
        contentPadding = PaddingValues(Spacing.S900),
        showBackground = true
    ) {
        CompositionLocalProvider(LocalContentColor provides SpyfallTheme.colorScheme.text.color) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.S500)) {
                Text(
                    text = group.name,
                    fontFamily = FontFamilyToken.Poppins.fontFamily,
                    fontWeight = FontWeight.W700,
                    fontSize = 48.sp,
                    color = ColorPrimitive.Black800.color
                )
                Divider(color = ColorPrimitive.Black800.color, modifier = Modifier.fillMaxWidth())
                PreviewRow(
                    token = {
                        Text("Token", style = SpyfallTheme.typography.Heading.H600.style)
                    },
                    specs = {
                        Text(
                            "Specs",
                            style = SpyfallTheme.typography.Heading.H600.style,
                            color = ColorPrimitive.Black600.color
                        )
                    },
                    example = {
                        Text(
                            "Example",
                            style = SpyfallTheme.typography.Heading.H600.style,
                            color = ColorPrimitive.Black600.color
                        )
                    }
                )
                for ((token, sampleText) in group.tokens) {
                    Divider(
                        color = ColorPrimitive.Black400.color,
                        modifier = Modifier
                            .fillMaxWidth(2f / 6f)
                            .padding(end = Spacing.S900)
                    )
                    PreviewRow(
                        token = {
                            Text(
                                text = token.identifier,
                                style = SpyfallTheme.typography.Heading.H700.style
                            )
                        },
                        specs = {
                            val family = when (token.fontFamily) {
                                FontFamilyToken.Poppins -> "Poppins"
                            }
                            val style = when (val weight = token.fontWeight) {
                                FontWeight.Bold -> "Bold"
                                FontWeight.SemiBold -> "Semibold"
                                FontWeight.Medium -> "Medium"
                                else -> "W${weight.weight}"
                            } + if (token.style.fontStyle == FontStyle.Italic) " Italic" else ""
                            Text(
                                """
                                $family
                                $style
                                Size: ${NumericalValues.getValue(token.fontSize.value.dp)} (${token.fontSize.value.roundToInt()}sp)  
                                Line: ${NumericalValues.getValue(token.lineHeight.value.dp)} (${token.lineHeight.value.roundToInt()}sp)  
                                """.trimIndent(),
                                style = SpyfallTheme.typography.Body.B600.style,
                                color = ColorPrimitive.Black600.color
                            )
                        },
                        example = {
                            Box(
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                                    .background(
                                        ColorPrimitive.Black200.color,
                                        shape = Radii.R400.shape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sampleText, style = token.style, textAlign = TextAlign.Center)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewRow(
    token: @Composable () -> Unit,
    specs: @Composable () -> Unit,
    example: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Box(Modifier.weight(1f)) { token() }
        Box(Modifier.weight(1f)) { specs() }
        Box(Modifier.weight(4f)) { example() }
    }
}

private data class PreviewGroup(
    val name: String,
    val tokens: List<Token>,
) {

    data class Token(
        val typographyToken: TypographyToken,
        val sampleText: String,
    )
}

private class PreviewGroupPreviewParameterProvider : PreviewParameterProvider<PreviewGroup> {
    override val values: Sequence<PreviewGroup> = sequenceOf(
        PreviewGroup(
            name = "Display",
            listOf(
                PreviewGroup.Token(SpyfallTheme.typography.Display.D1500, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Display.D1400, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Display.D1300, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Display.D1200, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Display.D1100, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Display.D1000, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Display.D900, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Display.D800, "Welcome to Spyfall")
            )
        ),

        PreviewGroup(
            name = "Heading",
            listOf(
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H1100, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H1200, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H1000, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H900, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H800, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H700, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H600, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H500, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Heading.H400, "Welcome to Spyfall")
            )
        ),

        PreviewGroup(
            name = "Label",
            listOf(
                PreviewGroup.Token(SpyfallTheme.typography.Label.L800, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Label.L700, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Label.L600, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Label.L500, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Label.L400, "Welcome to Spyfall")

            )
        ),

        PreviewGroup(
            name = "Body",
            listOf(
                PreviewGroup.Token(SpyfallTheme.typography.Body.B800, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Body.B700, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Body.B700.Italic, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Body.B600, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Body.B600.Italic, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Body.B500, "Welcome to Spyfall"),
                PreviewGroup.Token(SpyfallTheme.typography.Body.B500.Italic, "Welcome to Spyfall")
            )
        )
    )
}

fun TextStyle.toTypographyToken() = TypographyToken(
    fontFamily = FontFamilyToken.entries.firstOrNull { it.fontFamily == this.fontFamily } ?: FontFamilyToken.Poppins,
    fontWeight = this.fontWeight ?: FontWeight.Normal,
    fontSize = fontSize,
    lineHeight = lineHeight,
    lineBreak = lineBreak ?: LineBreak.Simple,
    fontStyle = fontStyle ?: FontStyle.Normal,
    identifier = "custom"
)
