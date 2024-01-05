package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.getBoldSpan
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.typography.TypographyToken

@Composable
fun BoldPrefixedText(
    boldText: String,
    regularText: String,
    textAlign: TextAlign = TextAlign.Start,
    typography: TypographyToken = OddOneOutTheme.typography.Body.B800
) {
    val annotatedString = getBoldSpan(fullString = "$boldText $regularText", boldString = boldText)
    Row {
       Text(
           textAlign = textAlign,
           text = annotatedString,
           typographyToken = typography
       )
    }
}

@Preview
@Composable
fun BoldPrefixedTextPreview() {
    PreviewContent {
        BoldPrefixedText(
            boldText = "Role: ",
            regularText = "Spy",
            typography = OddOneOutTheme.typography.Body.B800
        )
    }
}