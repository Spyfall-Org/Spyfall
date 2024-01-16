package com.dangerfield.libraries.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import oddoneout.core.throwIfDebug

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