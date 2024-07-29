package com.dangerfield.features.gameplay.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.ads.OddOneOutAd.MultiPlayerGamePlayBanner
import com.dangerfield.features.ads.ui.AdBanner
import com.dangerfield.features.gameplay.RoleCard
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.Fake
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.HorizontalSpacerD800
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.icon.CircleIcon
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.gameplay.internal.R

// TODO cleanup
// can we reduce params, and reduce logic in the view?
@Composable
fun GamePlayScreen(
    players: List<DisplayablePlayer>,
    locations: List<String>,
    timeRemaining: String,
    role: String,
    mePlayerId: String,
    isOddOneOut: Boolean,
    location: String?,
    packItem: PackItem?,
    videoCallLink: String?,
    onVideoCallButtonClicked: (String) -> Unit,
    isTimeUp: Boolean,
    canControlGame: Boolean,
    isLoadingVote: Boolean,
    hasMePlayerSubmittedVote: Boolean,
    gameResult: GameResult?,
    onVotingQuestionClicked: (hasVoted: Boolean) -> Unit,
    onGamePlayQuestionClicked: () -> Unit,
    onSubmitPlayerVoteClicked: (id: String) -> Unit,
    onEndGameClicked: () -> Unit,
    onResetGameClicked: () -> Unit,
    onSubmitLocationVoteClicked: (location: String) -> Unit,
    onTimeToVote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    var showLeaveGameDialog by remember { mutableStateOf(false) }

    BackHandler {
        showLeaveGameDialog = !showLeaveGameDialog
    }

    LaunchedEffect(isTimeUp) {
        if (isTimeUp) {
            scrollState.animateScrollTo(0)
            onTimeToVote()
        }
    }

    LaunchedEffect(gameResult) {
        if (gameResult != null) scrollState.animateScrollTo(0)
    }

    Screen(
        modifier = modifier,
        topBar = {
            Column {
                AdBanner(ad = MultiPlayerGamePlayBanner)
                ActionButtons(
                    isTimeUp = isTimeUp,
                    onVotingQuestionClicked = {
                        onVotingQuestionClicked(hasMePlayerSubmittedVote)
                    },
                    onGamePlayQuestionClicked = onGamePlayQuestionClicked,
                    videoCallLink = videoCallLink,
                    onVideoCallButtonClicked = onVideoCallButtonClicked,

                    )
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {
            GamePlayScreenContent(
                modifier = modifier,
                scrollState = scrollState,
                isTimeUp = isTimeUp,
                hasMePlayerSubmittedVote = hasMePlayerSubmittedVote,
                gameResult = gameResult,
                timeRemaining = timeRemaining,
                isOddOneOut = isOddOneOut,
                role = role,
                location = location,
                isVotingOnOddOneOut = !isOddOneOut,
                players = players,
                mePlayerId = mePlayerId,
                isVotingOnLocation = isOddOneOut,
                locations = locations,
                isLoadingVote = isLoadingVote,
                onSubmitPlayerVoteClicked = onSubmitPlayerVoteClicked,
                onSubmitLocationVoteClicked = onSubmitLocationVoteClicked,
                onResetGameClicked = onResetGameClicked,
                onEndGameClicked = onEndGameClicked,
                canControlGame = canControlGame,
                packItem = packItem
            )

            if (showLeaveGameDialog) {
                GamePlayLeaveDialog(
                    onDismissRequest = { showLeaveGameDialog = false },
                    onLeaveConfirmed = onEndGameClicked
                )
            }
        }
    }
}

@Composable
private fun GamePlayScreenContent(
    modifier: Modifier,
    scrollState: ScrollState,
    isTimeUp: Boolean,
    hasMePlayerSubmittedVote: Boolean,
    gameResult: GameResult?,
    timeRemaining: String,
    isOddOneOut: Boolean,
    role: String,
    location: String?,
    packItem: PackItem?,
    isVotingOnOddOneOut: Boolean,
    players: List<DisplayablePlayer>,
    mePlayerId: String,
    isVotingOnLocation: Boolean,
    locations: List<String>,
    isLoadingVote: Boolean,
    canControlGame: Boolean,
    onSubmitPlayerVoteClicked: (id: String) -> Unit,
    onSubmitLocationVoteClicked: (location: String) -> Unit,
    onResetGameClicked: () -> Unit,
    onEndGameClicked: () -> Unit
) {
    var selectedPlayerForVote by remember { mutableStateOf<DisplayablePlayer?>(null) }
    var selectedLocationForVote by remember { mutableStateOf<String?>(null) }
    val hasSelectedVote: Boolean = selectedPlayerForVote != null || selectedLocationForVote != null
    var isRoleHidden by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(OddOneOutTheme.colors.background.color)
            .padding(horizontal = Dimension.D1000)
            .verticalScroll(scrollState)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Header(
            hasMePlayerSubmittedVote = hasMePlayerSubmittedVote,
            isTimeUp = isTimeUp,
            gameResult = gameResult,
            timeRemaining = timeRemaining,
            isMePlayerOddOneOut = isOddOneOut
        )

        RoleCard(
            role = role,
            packItem = packItem,
            text = if (isOddOneOut) {
                dictionaryString(R.string.gamePlay_oddOneRoleTip_text)
            } else {
                dictionaryString(R.string.gamePlay_playerRoleTip_text)
            },
            isTheOddOneOut = isOddOneOut,
            isVisible = !isRoleHidden,
            onHideShowClicked = { isRoleHidden = !isRoleHidden },
        )

        Spacer(modifier = Modifier.height(Dimension.D1000))

        AnimatedVisibility(visible = !isTimeUp || isVotingOnOddOneOut) {
            PlayerList(
                isTimeUp = isTimeUp,
                hasMePlayerSubmittedVote = hasMePlayerSubmittedVote,
                selectedPlayer = selectedPlayerForVote,
                players = players,
                mePlayerId = mePlayerId,
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
                    && (players.find { it.isOddOneOut } != null || !isVotingOnOddOneOut)
        ) {
            CorrectAnswer(
                isVotingOnLocation = isVotingOnLocation,
                location = location.orEmpty(),
                oddOneOutName = players.find { it.isOddOneOut }?.name.orEmpty()
            )
        }

        VerticalSpacerD1200()

        if (canControlGame) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onResetGameClicked
            ) {
                Text(text = dictionaryString(R.string.gamePlay_restart_action))
            }

            VerticalSpacerD800()
        }

        Button(
            type = ButtonType.Secondary,
            modifier = Modifier.fillMaxWidth(),
            onClick = onEndGameClicked
        ) {
            Text(text = dictionaryString(R.string.gamePlay_end_action))
        }

        VerticalSpacerD1200()
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
                enabled = hasSelectedVote,
                modifier = Modifier.fillMaxWidth(),
                style = ButtonStyle.Background,
                onClick = {
                    if (selectedPlayer != null) {
                        onSubmitPlayerVoteClicked(selectedPlayer.id)
                    } else if (selectedLocation != null) {
                        onSubmitLocationVoteClicked(selectedLocation)
                    }
                }
            ) {
                Text(text = dictionaryString(R.string.gamePlay_submitVote_action))
            }
        }

        VerticalSpacerD1200()

        Text(
            text = dictionaryString(R.string.gamePlay_votingOr_text),
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
            hasMePlayerSubmittedVote -> dictionaryString(R.string.gamePlay_yourVote_header)
            isTimeUp -> dictionaryString(R.string.gamePlay_voteForLocation_header)
            else -> dictionaryString(R.string.gamePlay_locations_header)
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            typography = OddOneOutTheme.typography.Display.D800
        )

        VerticalSpacerD500()

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

        VerticalSpacerD1200()
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
            text = dictionaryString(
                R.string.gamePlay_correctAnswer_header,
                "type" to if (isVotingOnLocation) {
                    dictionaryString(R.string.app_location_label)
                } else {
                    dictionaryString(
                        R.string.app_oddOneOut_label
                    )
                }
            ),
            typography = OddOneOutTheme.typography.Display.D800
        )

        VerticalSpacerD500()

        GamePlayGrid(
            items = if (isVotingOnLocation) listOf(location) else listOf(oddOneOutName),
            isDisplayingForSelection = true,
            onItemSelectedForVote = {},
            isClickEnabled = false,
        )
        VerticalSpacerD1200()
    }
}

@Composable
private fun PlayerList(
    isTimeUp: Boolean,
    mePlayerId: String,
    hasMePlayerSubmittedVote: Boolean,
    selectedPlayer: DisplayablePlayer?,
    players: List<DisplayablePlayer>,
    onPlayerSelectedForVote: (DisplayablePlayer?) -> Unit
) {
    Column {
        val text = when {
            hasMePlayerSubmittedVote -> dictionaryString(id = R.string.gamePlay_yourVote_header)
            isTimeUp -> dictionaryString(R.string.gamePlay_voteForOddOneOut_header)
            else -> dictionaryString(R.string.gamePlay_players_header)
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            typography = OddOneOutTheme.typography.Display.D800
        )

        VerticalSpacerD500()

        val playersToShow = when {
            hasMePlayerSubmittedVote -> listOf(selectedPlayer)
            isTimeUp -> players.filter { it.id != mePlayerId }
            else -> players
        }

        GamePlayGrid(
            items = playersToShow.map { it?.name.orEmpty() },
            indexOfFirst = players.indexOfFirst { it.isFirst },
            isDisplayingForSelection = isTimeUp,
            onItemSelectedForVote = { index ->
                onPlayerSelectedForVote(index?.let { playersToShow[it] })
            },
            isClickEnabled = !hasMePlayerSubmittedVote,
        )

        VerticalSpacerD1200()
    }
}

@Composable
private fun ActionButtons(
    modifier: Modifier = Modifier,
    isTimeUp: Boolean,
    videoCallLink: String?,
    onVotingQuestionClicked: () -> Unit,
    onVideoCallButtonClicked: (String) -> Unit,
    onGamePlayQuestionClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Dimension.D500, top = Dimension.D500, end = Dimension.D500),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        if (videoCallLink != null) {
            CircleIcon(
                icon = SpyfallIcon.VideoCall(""),
                iconSize = IconSize.Medium,
                padding = Dimension.D200,
                backgroundColor = OddOneOutTheme.colors.surfacePrimary,
                contentColor = OddOneOutTheme.colors.onSurfacePrimary,
                onClick = { onVideoCallButtonClicked(videoCallLink) }
            )

            HorizontalSpacerD800()
        }

        CircleIcon(
            icon = SpyfallIcon.Question(""),
            iconSize = IconSize.Medium,
            padding = Dimension.D200,
            backgroundColor = OddOneOutTheme.colors.surfacePrimary,
            contentColor = OddOneOutTheme.colors.onSurfacePrimary,
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
    timeRemaining: String,
    isMePlayerOddOneOut: Boolean
) {
    val headerText = when {
        gameResult == GameResult.PlayersWon -> dictionaryString(id = R.string.gameResults_playersWonResult_text)
        gameResult == GameResult.OddOneOutWon && isMePlayerOddOneOut -> dictionaryString(R.string.gameResults_youWon_text)
        gameResult == GameResult.OddOneOutWon -> dictionaryString(id = R.string.gameResults)
        gameResult == GameResult.Draw -> dictionaryString(id = R.string.gameResults_tieResult_text)
        hasMePlayerSubmittedVote -> dictionaryString(R.string.gameResults_countingVotes_text)
        isTimeUp -> dictionaryString(R.string.gameResults_timeToVote_text)
        else -> timeRemaining
    }

    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = headerText,
        typography = OddOneOutTheme.typography.Display.D1100
    )
}


@Composable
@Preview
private fun PreviewGamePlayScreen() {
    Preview {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { _ -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = false,
            hasMePlayerSubmittedVote = false,
            gameResult = null,
            onTimeToVote = {},
            onEndGameClicked = {},
            onResetGameClicked = {},
            videoCallLink = "https:zoom.com",
            onVideoCallButtonClicked = {},
            mePlayerId = "1",
            canControlGame = true,
            packItem = PackItem.Celebrity("Joe")
        )
    }
}

@Composable
@Preview
private fun PreviewGamePlayScreenVoting() {
    Preview {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { _ -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = true,
            hasMePlayerSubmittedVote = false,
            gameResult = null,
            onTimeToVote = {},
            onEndGameClicked = {},
            onResetGameClicked = {},
            videoCallLink = "https:zoom.com",
            onVideoCallButtonClicked = {},
            mePlayerId = "1",
            canControlGame = true,
            packItem = PackItem.Celebrity("Joe")
        )
    }
}

@Composable
@Preview
private fun PreviewGamePlayScreenVoted() {
    Preview {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { _ -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = true,
            hasMePlayerSubmittedVote = true,
            gameResult = null,
            onTimeToVote = {},
            onEndGameClicked = {},
            onResetGameClicked = {},
            videoCallLink = "https:zoom.com",
            onVideoCallButtonClicked = {},
            mePlayerId = "1",
            canControlGame = true,
            packItem = PackItem.Celebrity("Joe")
        )
    }
}

@Composable
@Preview
private fun PreviewGamePlayScreenResults() {
    Preview {
        val players = Fake().players

        GamePlayScreen(
            players = players,
            locations = listOf("The Beach", "The Park", "The Mall"),
            timeRemaining = "1:32",
            role = "The Odd One Out",
            isOddOneOut = true,
            onVotingQuestionClicked = { _ -> },
            onGamePlayQuestionClicked = { -> },
            isLoadingVote = false,
            onSubmitPlayerVoteClicked = { },
            onSubmitLocationVoteClicked = { },
            location = "Something",
            isTimeUp = true,
            hasMePlayerSubmittedVote = true,
            gameResult = GameResult.Draw,
            onTimeToVote = {},
            onEndGameClicked = {},
            onResetGameClicked = {},
            videoCallLink = "https:zoom.com",
            onVideoCallButtonClicked = {},
            mePlayerId = "1",
            canControlGame = true,
            packItem = PackItem.Celebrity("Joe")
        )
    }
}
