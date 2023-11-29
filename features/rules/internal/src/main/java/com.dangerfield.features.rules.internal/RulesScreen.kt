package com.dangerfield.features.rules.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.dangerfield.libraries.ui.HorizontalSpacerS800
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS100
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.components.Divider
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.getBoldSpan
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.components.Screen

@Composable
fun RulesScreen(
    modifier: Modifier = Modifier
) {
    Screen(
        modifier = modifier,
        header = {
            Header("Rules")
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = Spacing.S800)
        ) {
            Title("Objective:")

            VerticalSpacerS500()

            BulletRow {
                BoldPrefixedText(
                    boldText = "Regular Players:",
                    regularText = "Uncover who among you is the 'Odd One Out'."
                )
            }

            VerticalSpacerS500()

            BulletRow {
                BoldPrefixedText(
                    boldText = "The 'Odd One Out':",
                    regularText = "Deduce the secret location without being discovered."
                )
            }

            VerticalSpacerS500()

            Divider()

            VerticalSpacerS500()

            Title("Starting a game:")

            VerticalSpacerS100()

            SubTitle("Create a Game", )

            VerticalSpacerS500()

            BulletRow {
                BoldPrefixedText(
                    boldText = "Select Location Packs:",
                    regularText = "Each pack contains a unique set of locations to play with."
                )
            }

            VerticalSpacerS500()

            BulletRow {
                BoldPrefixedText(
                    boldText = "Pick a time limit:",
                    regularText = "Choose how long players have to complete their objective."
                )
            }

            VerticalSpacerS500()

            BulletRow {
                BoldPrefixedText(
                    boldText = "Regular or Single device",
                    regularText = "Play with everyone on their own device or on a single device shared among players (more instructions at bottom)."
                )
            }

            VerticalSpacerS500()

            SubTitle("Join a Game", )

            VerticalSpacerS500()

            BulletRow {
                BoldPrefixedText(
                    boldText = "Enter The Access Code:",
                    regularText = "Each game has a unique access code. Enter it to join."
                )
            }

            BulletRow {
                BoldPrefixedText(
                    boldText = "Choose your name:",
                    regularText = "Names must be unique. Choose wisely."
                )
            }


        }
    }
}

@Composable
private fun Title(text: String) {
    Text(text = text, typographyToken = SpyfallTheme.typography.Heading.H800)
}

@Composable
private fun SubTitle(text: String) {
    Text(text = text, typographyToken = SpyfallTheme.typography.Body.B600.Bold)
}

@Composable
private fun Body(text: String) {
    Text(text = text, typographyToken = SpyfallTheme.typography.Body.B600)
}

@Composable
private fun Body(text: AnnotatedString) {
    Text(text = text, typographyToken = SpyfallTheme.typography.Body.B600)
}
@Composable
private fun BulletRow(
    content: @Composable () -> Unit = { }
) {
    Row {
        Text(text = "•")
        HorizontalSpacerS800()
        content()
    }
}

@Composable
private fun BoldPrefixedText(
    boldText: String,
    regularText: String
) {
    val annotatedString = getBoldSpan(fullString = "$boldText $regularText", boldString = boldText)
    Row {
        Body(text = annotatedString)
    }
}


@Composable
@ThemePreviews
private fun PreviewRulesScreen() {
    PreviewContent {
        RulesScreen()
    }
}