package spyfallx.coreui.typography

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import spyfallx.coreui.NumericalValues
import spyfallx.coreui.Spacing
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Radii
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.theme.SpyfallTheme
import kotlin.math.roundToInt

@Suppress("ComplexMethod")
open class TypographyToken internal constructor(
    internal val fontFamily: FontFamilyToken,
    internal val fontWeight: FontWeight,
    internal val fontSize: TextUnit,
    internal val lineHeight: TextUnit,
    internal val lineBreak: LineBreak,
    internal val fontStyle: FontStyle = FontStyle.Normal,
) {

    val style: TextStyle = TextStyle(
        fontFamily = fontFamily.fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontStyle = fontStyle,
        lineBreak = lineBreak
    )
}

object Typography {

    object Display {
        object D1100 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1100,
            lineHeight = LineHeight.H1200,
            lineBreak = LineBreak.Heading
        )

        object D1000 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S1000,
            lineHeight = LineHeight.H1100,
            lineBreak = LineBreak.Heading
        )

        object D900 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S900,
            lineHeight = LineHeight.H1000,
            lineBreak = LineBreak.Heading
        )

        object D800 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontSize.S800,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Heading
        )
    }

    object Heading {
        object H1000 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S1000,
            lineHeight = LineHeight.H1100,
            lineBreak = LineBreak.Heading
        )

        object H900 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S900,
            lineHeight = LineHeight.H1000,
            lineBreak = LineBreak.Heading
        )

        object H800 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S800,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Heading
        )

        object H700 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S700,
            lineHeight = LineHeight.H800,
            lineBreak = LineBreak.Heading
        )

        object H600 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S600,
            lineHeight = LineHeight.H700,
            lineBreak = LineBreak.Heading
        )

        object H500 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = FontSize.S500,
            lineHeight = LineHeight.H700,
            lineBreak = LineBreak.Heading
        )
    }

    object Label {
        object L700 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S700,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Paragraph
        )

        object L600 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S600,
            lineHeight = LineHeight.H800,
            lineBreak = LineBreak.Paragraph
        )

        object L500 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S500,
            lineHeight = LineHeight.H600,
            lineBreak = LineBreak.Paragraph
        )

        object L400 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S400,
            lineHeight = LineHeight.H500,
            lineBreak = LineBreak.Paragraph
        )
    }

    object Body {
        object B700 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S700,
            lineHeight = LineHeight.H900,
            lineBreak = LineBreak.Paragraph
        ) {
            object Italic : TypographyToken(
                fontFamily = B700.fontFamily,
                fontWeight = B700.fontWeight,
                fontSize = B700.fontSize,
                lineHeight = B700.lineHeight,
                lineBreak = B700.lineBreak,
                fontStyle = FontStyle.Italic
            )
        }

        object B600 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S600,
            lineHeight = LineHeight.H800,
            lineBreak = LineBreak.Paragraph
        ) {
            object Italic : TypographyToken(
                fontFamily = B600.fontFamily,
                fontWeight = B600.fontWeight,
                fontSize = B600.fontSize,
                lineHeight = B600.lineHeight,
                lineBreak = B600.lineBreak,
                fontStyle = FontStyle.Italic
            )
        }

        object B500 : TypographyToken(
            fontFamily = FontFamilyToken.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.S500,
            lineHeight = LineHeight.H700,
            lineBreak = LineBreak.Paragraph
        ) {
            object Italic : TypographyToken(
                fontFamily = B500.fontFamily,
                fontWeight = B500.fontWeight,
                fontSize = B500.fontSize,
                lineHeight = B500.lineHeight,
                lineBreak = B500.lineBreak,
                fontStyle = FontStyle.Italic
            )
        }
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
        CompositionLocalProvider(LocalContentColor provides SpyfallTheme.colorScheme.textPrimary.color) {
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
                        Text("Token", style = Typography.Heading.H600.style)
                    },
                    specs = {
                        Text(
                            "Specs",
                            style = Typography.Heading.H600.style,
                            color = ColorPrimitive.Black600.color
                        )
                    },
                    example = {
                        Text(
                            "Example",
                            style = Typography.Heading.H600.style,
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
                            val tokenName: String = when (token) {
                                Typography.Display.D1100 -> "disp-1100"
                                Typography.Display.D1000 -> "disp-1000"
                                Typography.Display.D900 -> "disp-900"
                                Typography.Display.D800 -> "disp-800"
                                Typography.Heading.H1000 -> "hed-1000"
                                Typography.Heading.H900 -> "hed-900"
                                Typography.Heading.H800 -> "hed-800"
                                Typography.Heading.H700 -> "hed-700"
                                Typography.Heading.H600 -> "hed-600"
                                Typography.Heading.H500 -> "hed-500"
                                Typography.Label.L700 -> "label-700"
                                Typography.Label.L600 -> "label-600"
                                Typography.Label.L500 -> "label-500"
                                Typography.Label.L400 -> "label-400"
                                Typography.Body.B700 -> "body-700"
                                Typography.Body.B700.Italic -> "body-700-italic"
                                Typography.Body.B600 -> "body-600"
                                Typography.Body.B600.Italic -> "body-600-italic"
                                Typography.Body.B500 -> "body-500"
                                Typography.Body.B500.Italic -> "body-500-italic"
                                else -> "unknown"
                            }
                            Text(
                                text = tokenName,
                                style = Typography.Heading.H700.style
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
                                style = Typography.Body.B600.style,
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
    constructor(
        groupName: String,
        vararg tokens: Pair<TypographyToken, String>,
    ) : this(groupName, tokens.map { (token, sampleText) -> Token(token, sampleText) })

    data class Token(
        val typographyToken: TypographyToken,
        val sampleText: String,
    )
}

private class PreviewGroupPreviewParameterProvider : PreviewParameterProvider<PreviewGroup> {
    override val values: Sequence<PreviewGroup> = sequenceOf(
        PreviewGroup(
            groupName = "Display",
            Typography.Display.D1100 to "Welcome to Spyfall",
            Typography.Display.D1000 to "Welcome to Spyfall",
            Typography.Display.D900 to "Welcome to Spyfall",
            Typography.Display.D800 to "Welcome to Spyfall"
        ),

        PreviewGroup(
            groupName = "Heading",
            Typography.Heading.H1000 to "",
            Typography.Heading.H900 to "Welcome to Spyfall",
            Typography.Heading.H800 to "Welcome to Spyfall",
            Typography.Heading.H700 to "Welcome to Spyfall",
            Typography.Heading.H600 to "Welcome to Spyfall",
            Typography.Heading.H500 to "Welcome to Spyfall"
        ),

        PreviewGroup(
            groupName = "Label",
            Typography.Label.L700 to "Welcome to Spyfall",
            Typography.Label.L600 to "Welcome to Spyfall",
            Typography.Label.L500 to "Welcome to Spyfall",
            Typography.Label.L400 to "Welcome to Spyfall"
        ),

        PreviewGroup(
            groupName = "Body",
            Typography.Body.B700 to "Welcome to Spyfall",
            Typography.Body.B700.Italic to "Welcome to Spyfall",
            Typography.Body.B600 to "Welcome to Spyfall",
            Typography.Body.B600.Italic to "Welcome to Spyfall",
            Typography.Body.B500 to "Welcome to Spyfall",
            Typography.Body.B500.Italic to "Welcome to Spyfall"
        )
    )
}
