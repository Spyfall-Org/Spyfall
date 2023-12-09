@file:Suppress("MaxLineLength")
package com.dangerfield.features.rules.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.dangerfield.libraries.ui.HorizontalSpacerS800
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS100
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.dragHandleTopAccessory
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.getBoldSpan
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import com.dangerfield.libraries.ui.Spacing

@Composable
fun RulesBottomSheet(
    onDismissRequest: (BottomSheetState) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: BottomSheetState = rememberBottomSheetState(),
) {
    BasicBottomSheet(
        onDismissRequest = { onDismissRequest(sheetState) },
        modifier = modifier,
        state = sheetState,
        showCloseButton = true,
        topAccessory = dragHandleTopAccessory(),
        topContent = {
            Text(text = "Rules")
        },
        content = {
            RulesContent()
        }
    )
}

@Composable
private fun RulesContent() {
    Column(
        Modifier
            .padding(horizontal = Spacing.S800)
    ) {
        Body(text = "Odd one out is a game of deception, misdirection, and deduction.")
        VerticalSpacerS800()

        Section(title = "Overview:") {
            Body(text = "At the start of the game all players are given a role at secret location, except for one player who is left in the dark (the odd one out).\n\nPlayers take turns asking each other questions about the location and their role in order to identify the odd one out without revealing the location to them.\n\nThe odd one out must play along, pretending to know the location while trying to deduce it without being discovered as the odd one out.")
        }

        Section(title = "Getting Started") {
            SubTitle("Join a Game")

            BulletRow {
                Body(text = "In order to join a game you must have the access code of an existing game. You can get this from the player who created it.")
            }

            VerticalSpacerS500()

            SubTitle("Create a Game")

            BulletRow {
                BoldPrefixedText(
                    boldText = "Select Location Packs:",
                    regularText = "Each pack contains a unique set of locations and roles to play with."
                )
            }

            BulletRow {
                BoldPrefixedText(
                    boldText = "Pick a time limit:",
                    regularText = "Choose how long players have to complete their objective. 5 minutes is reccomended for most games"
                )
            }

            BulletRow {
                BoldPrefixedText(
                    boldText = "Regular or Single device",
                    regularText = "Choose to play with everyone on their own device or on a single device shared among players."
                )
            }

            VerticalSpacerS500()

            Body(text = "Once a game is created an access code will be generated so that other players can join the game. Once all players are ready, anyone can start the game.")

        }

        Section(title = "Game play:") {
            Body(text = "As soon as the game starts players will be able to see:")

            VerticalSpacerS500()

            BulletRow {
                Body(text = "The randomly chosen location and their role (unless they are the odd one out)")
            }

            BulletRow {
                Body(text = "The time limit countdown")
            }

            BulletRow {
                Body(text = "A list of all players and potential game locations")
            }


            VerticalSpacerS500()

            SubTitle(text = "The Goal")

            BoldPrefixedText(
                boldText = "Players:",
                regularText = "To identify the odd one out without revealing the location to them."
            )

            VerticalSpacerS500()

            BoldPrefixedText(
                boldText = "The odd one out:",
                regularText = "To identify the location without revealing they are the odd one out"
            )

            VerticalSpacerS500()

            SubTitle(text = "Questioning")

            Body(text = "Players take turns asking each other questions about the location and their role.\n\nThe questions and answers should be vague enough to not reveal the location to the odd one out, but specific enough to identify the odd one out. The odd one out must play along and try not to get discovered while trying to deduce the location.")

            VerticalSpacerS500()

            Body(text = "One player will randomly be chose to go first")


            VerticalSpacerS800()

            SubTitle(text = "How to win")

            BulletRow {
                Body(text = "At any point the odd one out can decide to reveal themselves and guess the location. If they are wrong, the players win otherwise the odd one out wins.")
            }

            BulletRow {
                Body(text = "At any point the players can decide to stop the game and guess who the odd one out is. If they guess correctly the players win, otherwise the odd one out revels themselves and wins.")
            }

            BulletRow {
                Body(text = "After the timer runs out, the players must agree to accuse someone of being the odd one out. After the accusation the odd one out reveals themselves. If the players accused wrong, the odd one out wins, If the players accused correctly then the odd one out has a chance to tie the game by guessing the location correctly, otherwise the players win.")
            }

        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Title(text = title)
        VerticalSpacerS800()
        content()
        VerticalSpacerS1200()
    }
}

@Composable
private fun Title(text: String) {
    Column {
        Text(text = text, typographyToken = SpyfallTheme.typography.Heading.H800)
    }
}

@Composable
private fun SubTitle(text: String) {
    Column {
        Text(text = text, typographyToken = SpyfallTheme.typography.Body.B700.Bold)
        VerticalSpacerS100()
    }
}

@Composable
private fun Body(text: String) {
    Column {
        Text(text = text, typographyToken = SpyfallTheme.typography.Body.B600)
    }
}

@Composable
private fun Body(text: AnnotatedString) {
    Column {
        Text(text = text, typographyToken = SpyfallTheme.typography.Body.B600)
    }
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
        val sheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
        RulesBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {}
        )
    }
}