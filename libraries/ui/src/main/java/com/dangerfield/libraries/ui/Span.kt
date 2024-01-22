package com.dangerfield.libraries.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import oddoneout.core.throwIfDebug

const val ANNOTATED_STRING_URL_KEY = "URL"

@Composable
fun getBoldSpan(fullString: String, boldString: String): AnnotatedString = buildAnnotatedString {
    val startIndex = fullString.indexOf(boldString)
    val endIndex = startIndex + boldString.length

    append(fullString)

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String $fullString does not contain the specific text: $boldString"))
        return@buildAnnotatedString
    }

    addStyle(
        style = SpanStyle(fontWeight = FontWeight.W700),
        start = startIndex,
        end = endIndex
    )
    toAnnotatedString()
}

@Composable
fun getBoldUnderlinedSpan(fullString: String, boldString: String): AnnotatedString = buildAnnotatedString {
    val startIndex = fullString.indexOf(boldString)
    val endIndex = startIndex + boldString.length

    append(fullString)

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String $fullString does not contain the specific text: $boldString"))
        return@buildAnnotatedString
    }

    addStyle(
        style = SpanStyle(
            fontWeight = FontWeight.W700,
            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
        ),
        start = startIndex,
        end = endIndex
    )

    toAnnotatedString()
}

@Composable
fun String.getSpannableLink(
    linkText: String,
    url: String,
    style: SpanStyle = defaultStyle
) = makeLink(linkText = linkText, style = style, annotation = ANNOTATED_STRING_URL_KEY to url)

@Composable
fun String.makeLink(
    linkText: String,
    annotation: Pair<String, String>? = null,
    style: SpanStyle = defaultStyle
) = buildAnnotatedString {
    val startIndex = this@makeLink.indexOf(linkText)
    val endIndex = startIndex + linkText.length

    append(this@makeLink)

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String ${this@makeLink} does not contain the specific text: $linkText"))
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
fun AnnotatedString.makeLink(
    linkText: String,
    annotation: Pair<String, String>? = null,
    style: SpanStyle = defaultStyle
) = buildAnnotatedString {
    val startIndex = this@makeLink.indexOf(linkText)
    val endIndex = startIndex + linkText.length

    append(this@makeLink)

    if (startIndex < 0) {
        throwIfDebug(IllegalArgumentException("String ${this@makeLink} does not contain the specific text: $linkText"))
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
        color = OddOneOutTheme.colorScheme.text.color,
        fontWeight = FontWeight.ExtraBold,
        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
    )

@ThemePreviews()
@Composable
private fun MakeLinkPreview() {
    PreviewContent {
        Column {
            val makeLink = "This is normal content, but this is clickable".makeLink(
                "clickable"
            )
            val makeURL = "Get spannable: This is normal content, but this is clickable".getSpannableLink(
                "clickable",
                "http://www.thisisurl.com"
            )
            Text(makeLink, typographyToken = OddOneOutTheme.typography.Display.D1100)

            Text(makeURL, typographyToken = OddOneOutTheme.typography.Body.B600)

            val makeLinkDark = "This is normal content, but this is clickable".makeLink(
                "clickable",
                style = SpanStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )


            Text(makeLinkDark, typographyToken = OddOneOutTheme.typography.Body.B600)
        }
    }
}
