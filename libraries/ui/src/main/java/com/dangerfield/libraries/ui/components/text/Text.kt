package com.dangerfield.libraries.ui.components.text

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.LocalContentColor
import com.dangerfield.libraries.ui.LocalTypography
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.typography.TypographyResource

@Composable
fun S() {
    CompositionLocalProvider(LocalTypography provides OddOneOutTheme.typography.Body.B400.) {
        Text(text = "sdfsdf")
    }
}


@NonRestartableComposable
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    colorResource: ColorResource? = null,
    typography: TypographyResource = LocalTextConfig.current.typography ?: OddOneOutTheme.typography.Default,
    textDecoration: TextDecoration = LocalTextConfig.current.textDecoration ?: TextDecoration.None,
    textAlign: TextAlign? = LocalTextConfig.current.textAlign,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    overflow: TextOverflow = LocalTextConfig.current.overflow ?: DefaultTextOverflow,
    softWrap: Boolean = LocalTextConfig.current.softWrap ?: true,
    maxLines: Int = LocalTextConfig.current.maxLines ?: Int.MAX_VALUE,
    minLines: Int = LocalTextConfig.current.minLines ?: 1,
) {
    val style = typography.toStyle(colorResource, textDecoration, textAlign)

    BasicText(
        text = text.parseHtml(),
        modifier = modifier,
        style = style,
        overflow = overflow,
        onTextLayout = onTextLayout,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines
    )
}

@Composable
internal fun ProvideTextConfig(
    config: TextConfig,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTextConfig provides LocalTextConfig.current.merge(config), content = content)
}

@Composable
fun ProvideTextConfig(
    typography: TypographyResource? = null,
    color: ColorResource? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow? = null,
    softWrap: Boolean? = null,
    maxLines: Int? = null,
    minLines: Int? = null,
    content: @Composable () -> Unit,
) {
    ProvideTextConfig(
        config = LocalTextConfig.current.merge(
            color = color,
            typography = typography,
            textDecoration = textDecoration,
            textAlign = textAlign,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines
        ),
        content = content
    )
}

@NonRestartableComposable
@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: ColorResource? = null,
    typography: TypographyResource = LocalTextConfig.current.typography ?: OddOneOutTheme.typography.Default,
    textDecoration: TextDecoration = LocalTextConfig.current.textDecoration ?: TextDecoration.None,
    textAlign: TextAlign? = LocalTextConfig.current.textAlign,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    overflow: TextOverflow = LocalTextConfig.current.overflow ?: DefaultTextOverflow,
    softWrap: Boolean = LocalTextConfig.current.softWrap ?: true,
    maxLines: Int = LocalTextConfig.current.maxLines ?: Int.MAX_VALUE,
    minLines: Int = LocalTextConfig.current.minLines ?: 1,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = typography.toStyle(color, textDecoration, textAlign),
        overflow = overflow,
        onTextLayout = onTextLayout,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines
    )
}

@NonRestartableComposable
@Composable
fun Text(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    color: ColorResource? = null,
    typography: TypographyResource = LocalTextConfig.current.typography ?: OddOneOutTheme.typography.Default,
    textDecoration: TextDecoration = LocalTextConfig.current.textDecoration ?: TextDecoration.None,
    textAlign: TextAlign? = LocalTextConfig.current.textAlign,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    overflow: TextOverflow = LocalTextConfig.current.overflow ?: DefaultTextOverflow,
    softWrap: Boolean = LocalTextConfig.current.softWrap ?: true,
    maxLines: Int = LocalTextConfig.current.maxLines ?: Int.MAX_VALUE,
    minLines: Int = LocalTextConfig.current.minLines ?: 1,
) {

    Text(
        text = dictionaryString(text).parseHtml(),
        modifier = modifier,
        color = color,
        typography = typography,
        textDecoration = textDecoration,
        textAlign = textAlign,
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines
    )
}

internal val LocalTextConfig = compositionLocalOf { TextConfig.Default }

internal val DefaultTextOverflow = TextOverflow.Ellipsis

internal data class TextConfig(
    val typography: TypographyResource? = null,
    val color: ColorResource  = ColorResource.Unspecified,
    val textDecoration: TextDecoration? = null,
    val textAlign: TextAlign? = null,
    val overflow: TextOverflow? = null,
    val softWrap: Boolean? = null,
    val maxLines: Int? = null,
    val minLines: Int? = null,
) {
    companion object {
        val Default = TextConfig()
    }

    fun merge(other: TextConfig?): TextConfig =
        when {
            other == null || other == Default -> this
            this == Default -> other
            else ->
                merge(
                    typography = other.typography,
                    color = other.color,
                    textDecoration = other.textDecoration,
                    textAlign = other.textAlign,
                    overflow = other.overflow,
                    softWrap = other.softWrap,
                    maxLines = other.maxLines,
                    minLines = other.minLines
                )
        }

    fun merge(
        typography: TypographyResource?,
        color: ColorResource? = null,
        textDecoration: TextDecoration?,
        textAlign: TextAlign?,
        overflow: TextOverflow?,
        softWrap: Boolean?,
        maxLines: Int?,
        minLines: Int?,
    ): TextConfig =
        TextConfig(
            typography = typography ?: this.typography,
            color = color ?: this.color,
            textDecoration = textDecoration ?: this.textDecoration,
            textAlign = textAlign ?: this.textAlign,
            overflow = overflow ?: this.overflow,
            softWrap = softWrap ?: this.softWrap,
            maxLines = maxLines ?: this.maxLines,
            minLines = minLines ?: this.minLines
        )
}

@Composable
internal fun TypographyResource.toStyle(color: ColorResource?, textDecoration: TextDecoration?, textAlign: TextAlign?): TextStyle {
    val fallbackColor = LocalTextConfig.current.color.takeOrElse(LocalContentColor.current.takeOrElse(OddOneOutTheme.colors.text))

    return style.copy(
        color = color?.takeOrElse(fallbackColor)?.color ?: fallbackColor.color,
        textDecoration = textDecoration,
        textAlign = textAlign ?: TextAlign.Start
    )
}


private fun ColorResource.takeOrElse(default: ColorResource): ColorResource = this.takeIf { it.color.isSpecified } ?: default

@Preview
@Composable
private fun TextPreview() {
    Preview(
        contentPadding = PaddingValues(Dimension.D500),
        showBackground = true
    ) {
        Text(LoremIpsum(2).values.first())
    }
}

@Preview
@Composable
private fun TextPreviewProvided() {
    Preview(
        contentPadding = PaddingValues(Dimension.D500),
        showBackground = true
    ) {
        ProvideTextConfig(
            config = TextConfig(
                typography = OddOneOutTheme.typography.Default
                .copy(
                    fontWeight = FontWeight.ExtraLight
                ),
                color = OddOneOutTheme.colors.accent,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Start,
                maxLines = 1,
            )
        ) {
            Text(
                LoremIpsum(2).values.first(),
            )
        }
    }
}

