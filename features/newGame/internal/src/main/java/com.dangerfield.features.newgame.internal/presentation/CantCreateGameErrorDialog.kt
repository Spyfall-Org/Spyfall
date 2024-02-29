package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.runtime.Composable
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun CantCreateGameErrorDialog(
    onDismissRequest: () -> Unit
) {

    PageLogEffect(
        route = route("create_game_error_dialog"),
        type = PageType.Dialog
    )

    // TODO this is poping up when anything at all goes wrong on new game screen. Eitehr chagne the message or make the message dynamic
    BasicDialog(
        onDismissRequest = onDismissRequest,
        title = dictionaryString(R.string.app_somethingWentWrong_text),
        description = dictionaryString(R.string.newGame_createGameError_body),
        primaryButtonText = dictionaryString(id = R.string.app_okay_action),
        onPrimaryButtonClicked =  onDismissRequest
    )
}

@Composable
@Preview
private fun PreviewNewGameErrorDialog() {
    Preview {
        CantCreateGameErrorDialog(
            onDismissRequest = {},
        )
    }
}

