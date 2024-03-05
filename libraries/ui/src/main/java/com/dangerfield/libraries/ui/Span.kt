package com.dangerfield.libraries.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.dangerfield.libraries.ui.components.text.Text
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import oddoneout.core.throwIfDebug

const val ANNOTATED_STRING_URL_KEY = "URL"

fun String.makeBold(boldString: String): AnnotatedString = buildAnnotatedString {
    val startIndex = this@makeBold.indexOf(boldString)
    val endIndex = startIndex + boldString.length

    append(this@makeBold)

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String ${this@makeBold} does not contain the specific text: $boldString"))
        return@buildAnnotatedString
    }

    addStyle(
        style = SpanStyle(fontWeight = FontWeight.W700),
        start = startIndex,
        end = endIndex
    )
    toAnnotatedString()
}

fun String.addStyle(stringToStyle: String, style: SpanStyle): AnnotatedString {
    val startIndex = this.indexOf(stringToStyle)
    val endIndex = startIndex + stringToStyle.length

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String $this does not contain the specific text: $stringToStyle"))
        return buildAnnotatedString {
            append(this@addStyle)
        }
    }

    return buildAnnotatedString {
        append(this@addStyle)
        addStyle(
            style = style,
            start = startIndex,
            end = endIndex
        )
    }
}

@Composable
fun String.addClickableUrl(
    linkText: String,
    url: String,
    style: SpanStyle = defaultStyle
) = makeClickable(linkText = linkText, style = style, annotation = ANNOTATED_STRING_URL_KEY to url)

@Composable
fun String.makeClickable(
    linkText: String,
    annotation: Pair<String, String>? = null,
    style: SpanStyle = defaultStyle
) = buildAnnotatedString {
    val startIndex = this@makeClickable.indexOf(linkText)
    val endIndex = startIndex + linkText.length

    append(this@makeClickable)

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String ${this@makeClickable} does not contain the specific text: $linkText"))
        return@buildAnnotatedString
    }

    addStyle(
        style = style,
        start = startIndex,
        end = endIndex
    )
    if (annotation != null) {
        addStringAnnotation(
            tag = annotation.first,
            annotation = annotation.second,
            start = startIndex,
            end = endIndex
        )
    }
}

@Composable
fun AnnotatedString.makeClickable(
    linkText: String,
    annotation: Pair<String, String>? = null,
    style: SpanStyle = defaultStyle
) = buildAnnotatedString {
    val startIndex = this@makeClickable.indexOf(linkText)
    val endIndex = startIndex + linkText.length

    append(this@makeClickable)

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String ${this@makeClickable} does not contain the specific text: $linkText"))
        return@buildAnnotatedString
    }

    addStyle(
        style = style,
        start = startIndex,
        end = endIndex
    )
    if (annotation != null) {
        addStringAnnotation(
            tag = annotation.first,
            annotation = annotation.second,
            start = startIndex,
            end = endIndex
        )
    }
}

private val defaultStyle: SpanStyle
    @Composable
    @ReadOnlyComposable
    get() = SpanStyle(
        color = OddOneOutTheme.colors.text.color,
        fontWeight = FontWeight.ExtraBold,
        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
    )

@Preview()
@Composable
private fun MakeLinkPreview() {
    Preview {
        Column {
            val makeLink = "This is some random text. But this text is clickable".makeClickable(
                "But this text is clickable"
            )
            val makeURL = "makeWebLinkClickable: This is normal content, but this is a clickable url".addClickableUrl(
                "clickable url",
                "http://www.thisisurl.com"
            )

            Text(makeLink, typography = OddOneOutTheme.typography.Display.D1100)

            VerticalSpacerD800()

            Text(makeURL, typography = OddOneOutTheme.typography.Body.B600)

        }
    }
}
