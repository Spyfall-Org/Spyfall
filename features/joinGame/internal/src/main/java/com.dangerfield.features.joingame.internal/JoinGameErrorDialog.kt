package com.dangerfield.features.joingame.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import com.dangerfield.features.joingame.internal.UnresolvableError.IncompatibleError
import com.dangerfield.features.joingame.joinGameNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.preview.PreviewContent

@Composable
fun JoinGameErrorDialog(
    onDismiss: () -> Unit,
    onUpdateClicked: () -> Unit,
    unresolvableError: UnresolvableError
) {

    PageLogEffect(
        route = route("join_game_error"),
        type = PageType.Dialog,
        extras = bundleOf(
            "error_type" to unresolvableError.javaClass.simpleName
        )
    )

    val title = when (unresolvableError) {
        is IncompatibleError -> {
            "Looks like someone needs an update"
        }

        UnresolvableError.UnknownError -> {
            "Hmmmm..."
        }
    }

    val description = when (unresolvableError) {
        is IncompatibleError -> {
            if (unresolvableError.isCurrentLower) {
                "This game requires a newer version of the app. Please update to join this game."
            } else {
                "This game was created by someone with an older version of the app. All players will need to update and try again to play."
            }
        }

        UnresolvableError.UnknownError -> {
            "This is embarrassing, but something went wrong. We arent quite sure what. Please try again."
        }
    }

    val shouldShowUpdateButton =
        unresolvableError is IncompatibleError && unresolvableError.isCurrentLower

    BasicDialog(
        onDismissRequest = onDismiss,
        title = title,
        description = description,
        primaryButtonText = if (shouldShowUpdateButton) "Update" else "Ok",
        onPrimaryButtonClicked = {
            onDismiss()
            if (shouldShowUpdateButton) {
                onUpdateClicked()
            }
        }
    )
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogUpdate() {
    PreviewContent {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = IncompatibleError(isCurrentLower = true),
            onUpdateClicked = {}
        )
    }
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogSomeoneElseUpdate() {
    PreviewContent {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = IncompatibleError(isCurrentLower = false),
            onUpdateClicked = {}
        )
    }
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogUnknown() {
    PreviewContent {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = UnresolvableError.UnknownError,
            onUpdateClicked = {}
        )
    }
}