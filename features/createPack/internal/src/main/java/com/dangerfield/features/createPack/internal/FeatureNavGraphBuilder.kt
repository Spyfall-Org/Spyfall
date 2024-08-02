package com.dangerfield.features.createPack.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.createPack.createPackRoute
import com.dangerfield.libraries.game.PackItem
import se.ansman.dagger.auto.AutoBindIntoSet
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.bottomSheet
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import javax.inject.Inject

@AutoBindIntoSet
class FeatureNavGraphBuilder @Inject constructor(): FeatureNavBuilder {

    @OptIn(ExperimentalFoundationApi::class)
    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        bottomSheet(
            route = createPackRoute.navRoute,
            arguments = createPackRoute.navArguments
        ) {

            val sheetState = rememberBottomSheetState()

            fun dismissSheet() { router.dismissSheet(sheetState) }

            BottomSheet(
                showDragHandle = false,
                onDismissRequest = ::dismissSheet,
                state = sheetState
            ) {
                val pagerState = rememberPagerState(
                    pageCount = { 3 },
                    initialPage = 0
                )

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
                ) { page ->
                    when(page) {
                        0 -> {
                            NamePackScreen(
                                nameState = FieldState.Idle(""),
                                onNameChanged = {},
                                onNextClicked = { },
                                onNavigateBack = ::dismissSheet
                            )
                        }
                        1 -> AddItemsScreenPage()
                        2 -> ConfirmDetailsScreenPage()
                    }
                }
            }
        }
    }


    @Composable
    private fun AddItemsScreenPage(modifier: Modifier = Modifier) {
        AddItemsScreen(
            modifier = modifier,
            name = "My special pack",
            onNextClicked = { },
            onNavigateBack = {},
            maximumItems = 5,
            onAddItemClicked = {},
            items = listOf(
                PackItem.Custom("Item 1", roles = null),
            )
        )
    }

    @Composable
    private fun ConfirmDetailsScreenPage(modifier: Modifier = Modifier) {
        ConfirmDetailsScreen(
            modifier = modifier,
            isPublic = false,
            onIsPublicChanged = {  },
            onDone = { -> },
            onNavigateBack = { -> },
        )
    }
}