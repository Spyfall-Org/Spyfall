package com.dangerfield.features.waitingroom.internal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.ads.ui.AdBanner
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.DisplayablePlayer
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.HorizontalSpacerD800
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD1000
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.icon.CircleIcon
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.waitingroom.internal.R

@Composable
fun WaitingRoomScreen(
    modifier: Modifier = Modifier,
    accessCode: String,
    players: List<DisplayablePlayer>,
    minPlayers: Int,
    maxPlayers: Int,
    videoCallLink: String?,
    isLoadingRoom: Boolean,
    canControlGame: Boolean,
    isLoadingStart: Boolean,
    onChangeNameClicked: () -> Unit,
    onStartGameClicked: () -> Unit,
    onLeaveGameClicked: () -> Unit,
    onHelpClicked: () -> Unit,
    onCallLinkButtonClicked: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var showLeaveGameDialog by remember { mutableStateOf(false) }
    var showTooFewPlayersDialog by remember { mutableStateOf(false) }
    var showTooManyPlayersDialog by remember { mutableStateOf(false) }

    BackHandler {
        showLeaveGameDialog = !showLeaveGameDialog
    }

    Screen(
        modifier = modifier,
        topBar = {
            Column {
                AdBanner(ad = OddOneOutAd.WaitingRoomBanner)
                VerticalSpacerD500()
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    CircleIcon(
                        icon = SpyfallIcon.Question(""),
                        iconSize = IconSize.Medium,
                        padding = Dimension.D200,
                        backgroundColor = OddOneOutTheme.colors.surfacePrimary,
                        contentColor = OddOneOutTheme.colors.onSurfacePrimary,
                        onClick = onHelpClicked
                    )
                    HorizontalSpacerD800()
                }
            }
        }
    ) {
        Box {
            WaitingRoomScreenContent(
                scrollState = scrollState,
                it = it,
                accessCode = accessCode,
                videoCallLink = videoCallLink,
                onCallLinkButtonClicked = onCallLinkButtonClicked,
                isLoadingRoom = isLoadingRoom,
                players = players,
                isLoadingStart = isLoadingStart,
                onLeaveGameClicked = onLeaveGameClicked,
                onStartGameClicked = {
                    when {
                        players.size > maxPlayers -> showTooManyPlayersDialog = true
                        players.size < minPlayers -> showTooFewPlayersDialog = true
                        else -> onStartGameClicked()
                    }
                },
                onChangeNameClicked = onChangeNameClicked,
                canControlGame = canControlGame
            )

            if (showLeaveGameDialog) {
                WaitingRoomLeavingDialog(
                    onDismissRequest = { showLeaveGameDialog = false },
                    onLeaveConfirmed = onLeaveGameClicked
                )
            }

            if (showTooFewPlayersDialog) {
                TooFewPlayersDialog(
                    onDismissRequest = { showTooFewPlayersDialog = false },
                    onStartConfirmed = onStartGameClicked,
                    minPlayers = minPlayers,
                )
            }

            if (showTooManyPlayersDialog) {
                TooManyPlayersDialog(
                    onDismissRequest = { showTooManyPlayersDialog = false },
                    maxPlayers = maxPlayers,
                    playersSize = players.size
                )
            }
        }
    }
}

@Composable
private fun WaitingRoomScreenContent(
    scrollState: ScrollState,
    it: PaddingValues,
    accessCode: String,
    videoCallLink: String?,
    onCallLinkButtonClicked: (String) -> Unit,
    onChangeNameClicked: () -> Unit,
    isLoadingRoom: Boolean,
    players: List<DisplayablePlayer>,
    isLoadingStart: Boolean,
    canControlGame: Boolean,
    onLeaveGameClicked: () -> Unit,
    onStartGameClicked: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(it)
            .padding(horizontal = Dimension.D1000)
            .verticalScroll(scrollState)
    ) {

        VerticalSpacerD1000()

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = dictionaryString(R.string.waitingRoom_waiting_header),
            typography = OddOneOutTheme.typography.Display.D1000.Bold
        )

        VerticalSpacerD1000()

        Row {
            Text(
                text = dictionaryString(R.string.waitingRoom_accessCode_header),
                typography = OddOneOutTheme.typography.Default.Bold
            )
            Spacer(modifier = Modifier.width(Dimension.D500))
            SelectionContainer {
                Text(
                    modifier = Modifier.weight(1f),
                    text = accessCode,
                    typography = OddOneOutTheme.typography.Default
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            if (videoCallLink != null) {
                IconButton(
                    icon = SpyfallIcon.VideoCall(dictionaryString(R.string.waitingRoom_joinVideo_a11y)),
                    onClick = {
                        onCallLinkButtonClicked(videoCallLink)
                    }
                )
            }
        }

        VerticalSpacerD500()

        if (isLoadingRoom) {
            CircularProgressIndicator()
        } else {
            PlayerList(
                players = players,
                onChangeNameClicked = onChangeNameClicked
            )
        }

        VerticalSpacerD1200()

        if (!isLoadingStart) {

            if (canControlGame) {
                Button(
                    type = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStartGameClicked
                ) {
                    Text(text = dictionaryString(R.string.waitingRoom_startGame_action))
                }

                VerticalSpacerD1000()
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLeaveGameClicked,
                type = ButtonType.Secondary
            ) {
                Text(text = dictionaryString(R.string.waitingRoom_leaveGame_action))
            }

        } else {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        VerticalSpacerD1000()
    }
}

@Composable
private fun PlayerList(
    players: List<DisplayablePlayer>,
    onChangeNameClicked: () -> Unit,
) {

    players.forEachIndexed { index, player ->
        if (index != 0) {
            Spacer(modifier = Modifier.height(Dimension.D500))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    OddOneOutTheme.colors.surfacePrimary.color,
                    shape = Radii.Card.shape
                )
                .padding(horizontal = Dimension.D500)
                .padding(vertical = Dimension.D400),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${index + 1}")
            Spacer(modifier = Modifier.width(Dimension.D500))
            Text(text = player.name)
            if (player.isMe) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    icon = SpyfallIcon.Pencil("Change name"),
                    onClick = onChangeNameClicked
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewWaitingRoomScreen() {
    Preview {
        val players = listOf(
            DisplayablePlayer("Josiah", isMe = false),
            DisplayablePlayer("Arif", isMe = false),
            DisplayablePlayer("Michael", isMe = false),
            DisplayablePlayer("Eli", isMe = true),
            DisplayablePlayer("Nibraas", isMe = false),
            DisplayablePlayer("George", isMe = false)
        )
        WaitingRoomScreen(
            accessCode = "dsv311",
            players = players,
            isLoadingRoom = false,
            isLoadingStart = false,
            videoCallLink = "https://meet.google.com/lookup/abc123",
            onCallLinkButtonClicked = { },
            onStartGameClicked = {},
            onLeaveGameClicked = {},
            onChangeNameClicked = {},
            minPlayers = 3,
            maxPlayers = 6,
            onHelpClicked = {},
            canControlGame = true
        )
    }
}
