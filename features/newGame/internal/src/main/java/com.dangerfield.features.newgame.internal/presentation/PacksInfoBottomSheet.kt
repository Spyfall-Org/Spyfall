package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.newgame.internal.presentation.model.PackOption
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun PacksInfoBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    packs: List<PackOption.Pack>,
    onDismiss: (BottomSheetState) -> Unit
) {
    PageLogEffect(
        route = route("packs_info_bottom_sheet"), type = PageType.BottomSheet
    )

    BasicBottomSheet(onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        modifier = modifier,
        stickyTopContent = {
            Text(text = dictionaryString(R.string.newGame_packsInfo_header))
        },
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = dictionaryString(R.string.packsInfo_packsDescription_text),
                )
                if (packs.isNotEmpty()) {
                    VerticalSpacerD800()
                    packs.forEach { pack ->
                        Text(
                            text = pack.packName.orEmpty(),
                            typography = OddOneOutTheme.typography.Default
                        )
                        VerticalSpacerD500()
                        NonLazyVerticalGrid(
                            columns = 2, data = pack.pack.packItems
                        ) { _, item ->
                            BulletRow(modifier = Modifier.fillMaxWidth()) {
                                Text(text = item.name)
                            }
                        }

                        VerticalSpacerD1200()
                    }

                    VerticalSpacerD800()
                }
            }
        },
        stickyBottomContent = {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onDismiss(bottomSheetState) }) {
                Text(text = dictionaryString(id = R.string.app_okay_action))
            }
        })
}

@Composable
@Preview
private fun PreviewPackDetailsBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)

    Preview {
        PacksInfoBottomSheet(
            onDismiss = {},
            bottomSheetState = bottomSheetState,
            packs = listOf(
                PackOption.Pack(Pack.LocationPack.Fakes.Pack1),
                PackOption.Pack(Pack.LocationPack.Fakes.Pack3),
                PackOption.Pack(Pack.LocationPack.Fakes.Pack3),
            ),
        )
    }
}
