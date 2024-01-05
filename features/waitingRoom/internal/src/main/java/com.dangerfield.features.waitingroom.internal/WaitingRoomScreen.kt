package com.dangerfield.features.waitingroom.internal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
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
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS1000
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.color.background

@Composable
fun WaitingRoomScreen(
    modifier: Modifier = Modifier,
    accessCode: String,
    players: List<DisplayablePlayer>,
    videoCallLink: String?,
    isLoadingRoom: Boolean,
    isLoadingStart: Boolean,
    onChangeNameClicked: () -> Unit,
    onStartGameClicked: () -> Unit,
    onLeaveGameClicked: () -> Unit,
    onCallLinkButtonClicked: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var showLeaveGameDialog by remember { mutableStateOf(false) }

    BackHandler {
        showLeaveGameDialog = !showLeaveGameDialog
    }

    Screen(
        modifier = modifier,
        topBar = {
            Column {
                AdBanner(ad = OddOneOutAd.WaitingRoomBanner)
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
                onStartGameClicked = onStartGameClicked,
                onChangeNameClicked = onChangeNameClicked
            )

            if (showLeaveGameDialog) {
                WaitingRoomLeavingDialog(
                    onDismissRequest = { showLeaveGameDialog = false },
                    onLeaveConfirmed = onLeaveGameClicked
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
    onLeaveGameClicked: () -> Unit,
    onStartGameClicked: () -> Unit
) {

        ScrollingColumnWithFadingEdge(
            state = scrollState,
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Spacing.S1000)
        ) {

            VerticalSpacerS1000()

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Waiting for players...",
                typographyToken = OddOneOutTheme.typography.Display.D1000.Bold
            )

            VerticalSpacerS1000()

            Row {
                Text(
                    text = "Access Code:",
                    typographyToken = OddOneOutTheme.typography.Default.Bold
                )
                Spacer(modifier = Modifier.width(Spacing.S500))
                SelectionContainer {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = accessCode,
                        typographyToken = OddOneOutTheme.typography.Default
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                if (videoCallLink != null) {
                    IconButton(
                        icon = SpyfallIcon.VideoCall("Join game video call"),
                        onClick = {
                            onCallLinkButtonClicked(videoCallLink)
                        }
                    )
                }
            }

            VerticalSpacerS500()

            if (isLoadingRoom) {
                CircularProgressIndicator()
            } else {
                PlayerList(
                    players = players,
                    onChangeNameClicked = onChangeNameClicked
                )
            }

            VerticalSpacerS1200()

            if (!isLoadingStart) {

                Button(
                    type = ButtonType.Accent,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStartGameClicked
                ) {
                    Text(text = "Start Game")
                }

                VerticalSpacerS1000()

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLeaveGameClicked,
                    type = ButtonType.Regular
                ) {
                    Text(text = "Leave Game")
                }

            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            VerticalSpacerS1000()
        }
}

@Composable
private fun PlayerList(
    players: List<DisplayablePlayer>,
    onChangeNameClicked: () -> Unit,
) {
    players.forEachIndexed { index, player ->
        if (index != 0) {
            Spacer(modifier = Modifier.height(Spacing.S500))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    OddOneOutTheme.colorScheme.surfacePrimary,
                    radius = Radii.Card
                )
                .padding(horizontal = Spacing.S500)
                .padding(vertical = Spacing.S400)
        ) {
            Text(text = "${index + 1}")
            Spacer(modifier = Modifier.width(Spacing.S500))
            Text(text = player.name)
            if (player.isMe) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(icon = SpyfallIcon.Pencil("Change name"), onClick = onChangeNameClicked)
            }
        }
    }
}

@Composable
@Preview
fun PreviewWaitingRoomScreen() {
    PreviewContent {
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
            onChangeNameClicked = {}
        )
    }
}
