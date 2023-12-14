package com.dangerfield.features.gameplay.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.icon.CircularIcon
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.color.background

@Composable
fun GamePlayScreen(
    players: List<DisplayablePlayer>,
    locations: List<String>,
    timeRemaining: String,
    role: String,
    isOddOneOut: Boolean,
    location: String?,
    isTimeUp: Boolean,
    isLoadingVote: Boolean,
    hasMePlayerSubmittedVote: Boolean,
    gameResult: GameResult?,
    onVotingQuestionClicked: () -> Unit,
    onGamePlayQuestionClicked: () -> Unit,
    onSubmitPlayerVoteClicked: (id: String) -> Unit,
    onSubmitLocationVoteClicked: (location: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    var selectedPlayerForVote by rememberSaveable { mutableStateOf<DisplayablePlayer?>(null) }
    var selectedLocationForVote by rememberSaveable { mutableStateOf<String?>(null) }
    val hasSelectedVote: Boolean = selectedPlayerForVote != null || selectedLocationForVote != null
    val isVotingOnLocation = isOddOneOut
    val isVotingOnOddOneOut = !isVotingOnLocation

    LaunchedEffect(isTimeUp) {
        if (isTimeUp) scrollState.animateScrollTo(0)
    }

    LaunchedEffect(gameResult) {
        if (gameResult != null) scrollState.animateScrollTo(0)
    }

    Screen { padding ->
        ScrollingColumnWithFadingEdge(
            modifier = modifier
                .background(OddOneOutTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = Spacing.S1000),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scrollState
        ) {
            HelpButton(
                isTimeUp = isTimeUp,
                onVotingQuestionClicked = onVotingQuestionClicked,
                onGamePlayQuestionClicked = onGamePlayQuestionClicked
            )

            Header(
                hasMePlayerSubmittedVote = hasMePlayerSubmittedVote,
                isTimeUp = isTimeUp,
                gameResult = gameResult,
                timeRemaining = timeRemaining
            )

            RoleCard(
                role = role,
                location = location,
                text = if (isOddOneOut) "Don't get found out" else "Find the odd one out",
                isTheOddOneOut = isOddOneOut
            )

            Spacer(modifier = Modifier.height(Spacing.S1000))

            AnimatedVisibility(visible = !isTimeUp || isVotingOnOddOneOut) {
                PlayerList(
                    isTimeUp = isTimeUp,
                    hasMePlayerSubmittedVote = hasMePlayerSubmittedVote,
                    selectedPlayer = selectedPlayerForVote,
                    players = players,
                    onPlayerSelectedForVote = {
                        selectedPlayerForVote = it
                    }
                )
            }

            AnimatedVisibility(visible = !isTimeUp || isVotingOnLocation) {
                LocationsList(
                    hasMePlayerSubmittedVote = hasMePlayerSubmittedVote,
                    selectedLocation = selectedLocationForVote,
                    locations = locations,
                    isTimeUp = isTimeUp,
                    onLocationSelectedForVote = {
                        selectedLocationForVote = it
                    }
                )
            }

            AnimatedVisibility(visible = isTimeUp && !hasMePlayerSubmittedVote) {
                VoteButton(
                    isLoadingVote = isLoadingVote,
                    selectedPlayer = selectedPlayerForVote,
                    selectedLocation = selectedLocationForVote,
                    hasSelectedVote = hasSelectedVote,
                    onSubmitPlayerVoteClicked = onSubmitPlayerVoteClicked,
                    onSubmitLocationVoteClicked = onSubmitLocationVoteClicked
                )
            }

            AnimatedVisibility(
                visible =
                gameResult != null &&
                        (location != null || !isVotingOnLocation)
                        && (players.find { it.isOddOneOut } != null || !isVotingOnOddOneOut )
            ) {
                CorrectAnswer(
                    isVotingOnLocation = isVotingOnLocation,
                    location = location.orEmpty(),
                    oddOneOutName = players.find { it.isOddOneOut }?.name.orEmpty()
                )
            }

            VerticalSpacerS1200()

            Button(modifier = Modifier.fillMaxWidth(), onClick = { /*TODO*/ }) {
                Text(text = "Restart Game")
            }

            Spacer(modifier = Modifier.height(Spacing.S800))

            Button(type = ButtonType.Regular,
                modifier = Modifier.fillMaxWidth(),
                onClick = { /*TODO*/ }) {
                Text(text = "End Game")
            }

            VerticalSpacerS1200()
        }
    }
}

@Composable
private fun VoteButton(
    isLoadingVote: Boolean,
    selectedPlayer: DisplayablePlayer?,
    selectedLocation: String?,
    hasSelectedVote: Boolean,
    onSubmitPlayerVoteClicked: (id: String) -> Unit,
    onSubmitLocationVoteClicked: (location: String) -> Unit
) {
    Column {
        if (isLoadingVote) {
            Row(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator()
            }
        } else {
            Button(
                enabled = selectedPlayer != null || selectedLocation != null,
                modifier = Modifier.fillMaxWidth(),
                style = if (hasSelectedVote) ButtonStyle.Filled else ButtonStyle.Outlined,
                onClick = {
                    if (selectedPlayer != null) {
                        onSubmitPlayerVoteClicked(selectedPlayer.id)
                    } else if (selectedLocation != null) {
                        onSubmitLocationVoteClicked(selectedLocation)
                    }
                }
            ) {
                Text(text = "Submit Vote")
            }
        }

        VerticalSpacerS1200()

        Text(
            text = "or",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LocationsList(
    hasMePlayerSubmittedVote: Boolean,
    selectedLocation: String?,
    locations: List<String>,
    isTimeUp: Boolean,
    onLocationSelectedForVote: (String?) -> Unit
) {
    Column {

        val text = when {
            hasMePlayerSubmittedVote -> "Your Vote:"
            isTimeUp -> "Which location are you at?"
            else -> "Locations:"
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            typographyToken = OddOneOutTheme.typography.Display.D800
        )

        VerticalSpacerS500()

        val locationsToShow = if (hasMePlayerSubmittedVote) {
            listOf(selectedLocation.orEmpty())
        } else {
            locations
        }

        GamePlayGrid(
            items = locationsToShow,
            isDisplayingForSelection = isTimeUp,
            onItemSelectedForVote = { index ->
                onLocationSelectedForVote(index?.let { locations[index] })
            },
            isClickEnabled = !hasMePlayerSubmittedVote,
        )

        VerticalSpacerS1200()
    }
}

@Composable
private fun CorrectAnswer(
    isVotingOnLocation: Boolean,
    location: String,
    oddOneOutName: String
) {
    Column {
        Text(
            text = "The actual ${if (isVotingOnLocation) "location" else "odd one out"}:",
            typographyToken = OddOneOutTheme.typography.Display.D800
        )

        VerticalSpacerS500()

        GamePlayGrid(
            items = if (isVotingOnLocation) listOf(location) else listOf(oddOneOutName),
            isDisplayingForSelection = true,
            onItemSelectedForVote = {},
            isClickEnabled = false,
        )
        VerticalSpacerS1200()
    }
}

@Composable
private fun PlayerList(
    isTimeUp: Boolean,
    hasMePlayerSubmittedVote: Boolean,
    selectedPlayer: DisplayablePlayer?,
    players: List<DisplayablePlayer>,
    onPlayerSelectedForVote: (DisplayablePlayer?) -> Unit
) {
    Column {
        val text = when {
            hasMePlayerSubmittedVote -> "Your Vote:"
            isTimeUp -> "Which player is the odd one out?"
            else -> "Players:"
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            typographyToken = OddOneOutTheme.typography.Display.D800
        )

        VerticalSpacerS500()

        val namesToShow = if (hasMePlayerSubmittedVote) {
            listOf(selectedPlayer?.name.orEmpty())
        } else {
            players.map { it.name }
        }

        GamePlayGrid(
            items = namesToShow,
            indexOfFirst = players.indexOfFirst { it.isFirst },
            isDisplayingForSelection = isTimeUp,
            onItemSelectedForVote = { index ->
                onPlayerSelectedForVote(index?.let { players[it] })
            },
            isClickEnabled = !hasMePlayerSubmittedVote,
        )

        VerticalSpacerS1200()
    }
}

@Composable
private fun HelpButton(
    isTimeUp: Boolean,
    onVotingQuestionClicked: () -> Unit,
    onGamePlayQuestionClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.S50, top = Spacing.S1000)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        CircularIcon(
            icon = SpyfallIcon.Question(""),
            iconSize = IconSize.Medium,
            padding = Spacing.S200,
            backgroundColor = OddOneOutTheme.colorScheme.surfacePrimary,
            contentColor = OddOneOutTheme.colorScheme.onSurfacePrimary,
            onClick = {
                if (isTimeUp) onVotingQuestionClicked() else onGamePlayQuestionClicked()
            }
        )
    }
}

@Composable
private fun Header(
    hasMePlayerSubmittedVote: Boolean,
    isTimeUp: Boolean,
    gameResult: GameResult?,
    timeRemaining: String
) {
    val headerText = when {
        gameResult == GameResult.PlayersWon -> "The Players Have Won!"
        gameResult == GameResult.OddOneOutWon -> "The Odd One Out Has Won!"
        gameResult == GameResult.Draw -> "It's a Draw!"
        hasMePlayerSubmittedVote -> "Counting votes..."
        isTimeUp -> "Time to vote!"
        else -> timeRemaining
    }

    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = headerText,
        typographyToken = OddOneOutTheme.typography.Display.D1100
    )
}


@Composable
@Preview
private fun PreviewGamePlayScreen() {
    PreviewContent {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = false,
            hasMePlayerSubmittedVote = false,
            gameResult = null,
        )
    }
}

@Composable
@Preview
private fun PreviewGamePlayScreenVoting() {
    PreviewContent {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = true,
            hasMePlayerSubmittedVote = false,
            gameResult = null,
        )
    }
}

@Composable
@Preview
private fun PreviewGamePlayScreenVoted() {
    PreviewContent {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = true,
            hasMePlayerSubmittedVote = true,
            gameResult = null,
        )
    }
}

@Composable
@Preview
private fun PreviewGamePlayScreenResults() {
    PreviewContent {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = true,
            hasMePlayerSubmittedVote = true,
            gameResult = GameResult.Draw,
        )
    }
}
