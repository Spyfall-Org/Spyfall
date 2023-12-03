package com.dangerfield.libraries.ui.components.text

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.color.ColorPrimitive
import spyfallx.ui.color.ColorToken
import com.dangerfield.libraries.ui.color.LocalContentColor
import spyfallx.ui.color.takeOrElse
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import com.dangerfield.libraries.ui.typography.TypographyToken

@NonRestartableComposable
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    colorPrimitive: ColorPrimitive,
    typographyToken: TypographyToken = LocalTextConfig.current.typographyToken ?: SpyfallTheme.typography.Default,
    textDecoration: TextDecoration = LocalTextConfig.current.textDecoration ?: TextDecoration.None,
    textAlign: TextAlign? = LocalTextConfig.current.textAlign,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    overflow: TextOverflow = LocalTextConfig.current.overflow ?: DefaultTextOverflow,
    softWrap: Boolean = LocalTextConfig.current.softWrap ?: true,
    maxLines: Int = LocalTextConfig.current.maxLines ?: Int.MAX_VALUE,
    minLines: Int = LocalTextConfig.current.minLines ?: 1,
) {
    Text(
        text = text,
        modifier = modifier,
        typographyToken = typographyToken,
        textDecoration = textDecoration,
        textAlign = textAlign,
        color = ColorToken.Color("color", colorPrimitive),
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
    text: String,
    modifier: Modifier = Modifier,
    color: ColorToken.Color? = null,
    typographyToken: TypographyToken = LocalTextConfig.current.typographyToken ?: SpyfallTheme.typography.Default,
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
        style = typographyToken.toStyle(color, textDecoration, textAlign),
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
    typographyToken: TypographyToken? = null,
    color: ColorToken.Color? = null,
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
            typographyToken = typographyToken,
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
    color: ColorToken.Color? = null,
    typographyToken: TypographyToken = LocalTextConfig.current.typographyToken ?: SpyfallTheme.typography.Default,
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
        style = typographyToken.toStyle(color, textDecoration, textAlign),
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
    color: ColorToken.Color? = null,
    typographyToken: TypographyToken = LocalTextConfig.current.typographyToken ?: SpyfallTheme.typography.Default,
    textDecoration: TextDecoration = LocalTextConfig.current.textDecoration ?: TextDecoration.None,
    textAlign: TextAlign? = LocalTextConfig.current.textAlign,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    overflow: TextOverflow = LocalTextConfig.current.overflow ?: DefaultTextOverflow,
    softWrap: Boolean = LocalTextConfig.current.softWrap ?: true,
    maxLines: Int = LocalTextConfig.current.maxLines ?: Int.MAX_VALUE,
    minLines: Int = LocalTextConfig.current.minLines ?: 1,
) {

    Text(
        text = stringResource(text),
        modifier = modifier,
        color = color,
        typographyToken = typographyToken,
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
    val typographyToken: TypographyToken? = null,
    val color: ColorToken.Color? = null,
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
                    typographyToken = other.typographyToken,
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
        typographyToken: TypographyToken?,
        color: ColorToken.Color? = null,
        textDecoration: TextDecoration?,
        textAlign: TextAlign?,
        overflow: TextOverflow?,
        softWrap: Boolean?,
        maxLines: Int?,
        minLines: Int?,
    ): TextConfig =
        TextConfig(
            typographyToken = typographyToken ?: this.typographyToken,
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
internal fun TypographyToken.toStyle(color: ColorToken.Color?, textDecoration: TextDecoration?, textAlign: TextAlign?) =
    style.copy(
        color = color.takeOrElse { LocalTextConfig.current.color }.takeOrElse { LocalContentColor.current }.color,
        textDecoration = textDecoration,
        textAlign = textAlign
    )


@Preview
@Composable
private fun TextPreview() {
    PreviewContent(
        contentPadding = PaddingValues(Spacing.S500),
        showBackground = true
    ) {
        Text(LoremIpsum(2).values.first())
    }
}
