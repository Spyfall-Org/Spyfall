package com.dangerfield.features.createPack.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.createPack.createPackRoute
import com.dangerfield.features.createPack.internal.CreatePackViewModel.Action.SavePack
import com.dangerfield.features.createPack.internal.CreatePackViewModel.Action.UpdateIsPublic
import com.dangerfield.features.createPack.internal.CreatePackViewModel.Action.UpdateName
import com.dangerfield.features.createPack.internal.EditPackItemViewModel.Action.AddItemToPack
import com.dangerfield.features.createPack.internal.EditPackItemViewModel.Action.AddRole
import com.dangerfield.features.createPack.internal.EditPackItemViewModel.Action.RemoveRole
import com.dangerfield.features.createPack.internal.EditPackItemViewModel.Action.UpdateRoleName
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.PackItem
import se.ansman.dagger.auto.AutoBindIntoSet
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.bottomSheet
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.showDeveloperMessage
import com.dangerfield.libraries.ui.showMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import oddoneout.core.Message
import javax.inject.Inject

@AutoBindIntoSet
class FeatureNavGraphBuilder @Inject constructor() : FeatureNavBuilder {

    @OptIn(ExperimentalFoundationApi::class)
    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        bottomSheet(
            route = createPackRoute.navRoute,
            arguments = createPackRoute.navArguments
        ) {
            val viewModel: CreatePackViewModel = hiltViewModel()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()
            val sheetState = rememberBottomSheetState()
            val coroutineScope = rememberCoroutineScope()

            ObserveWithLifecycle(viewModel.eventFlow) { event ->
                when (event) {
                    CreatePackViewModel.Event.CouldNotLoadPack -> {
                        showMessage(
                            message = Message("Yikes, we have an uncaught bug. Please try again."),
                            debugMessage = Message("Could not load the pack for creation")
                        )
                    }
                    CreatePackViewModel.Event.PackCreated -> {
                        showMessage { "Your pack is ready to play with!" }
                        router.dismissSheet(sheetState)
                    }
                }
            }

            BottomSheet(
                showDragHandle = false,
                onDismissRequest = { router.dismissSheet(sheetState) },
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
                    when (page) {
                        0 -> NamePackPageScreen(
                            state = state,
                            viewModel = viewModel,
                            coroutineScope = coroutineScope,
                            pagerState = pagerState,
                            router = router,
                            sheetState = sheetState
                        )

                        1 -> AddItemsPageScreen(
                            state,
                            coroutineScope,
                            pagerState,
                            router
                        )

                        2 -> ConfirmDetailsScreen(
                            isPublic = state.isPublic,
                            onIsPublicChanged = { viewModel.takeAction(UpdateIsPublic(it)) },
                            onDone = { viewModel.takeAction(SavePack) },
                            onNavigateBack = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                        )
                    }
                }
            }
        }

        bottomSheet(
            route = editPackItemRoute.navRoute,
            arguments = editPackItemRoute.navArguments
        ) {
            val viewModel: EditPackItemViewModel = hiltViewModel()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()
            val sheetState = rememberBottomSheetState()

            ObserveWithLifecycle(viewModel.eventFlow) { event ->
                when (event) {
                    EditPackItemViewModel.Event.CouldNotAddItem -> {
                        showMessage { "Could not add item to pack. Please make sure all fields are valid." }
                    }
                    EditPackItemViewModel.Event.CouldNotLoadPack -> {
                        showDeveloperMessage { "Could not load pack. Please try again." }
                    }
                    EditPackItemViewModel.Event.ItemAdded -> {
                        router.dismissSheet(sheetState)
                    }
                }
            }

            EditItemBottomSheet(
                bottomSheetState = sheetState,
                nameFieldState = state.nameFieldState,
                onNameChanged = {
                    viewModel.takeAction(EditPackItemViewModel.Action.UpdateName(name = it))
                },
                roleFields = state.roleFields,
                onRoleChanged = { index, name ->
                    viewModel.takeAction(UpdateRoleName(index = index, name = name))
                },
                onDeleteRole = { viewModel.takeAction(RemoveRole(index = it)) },
                onAddRoleField = { viewModel.takeAction(AddRole) },
                isFormValid = state.isFormValid,
                onDismiss = { router.dismissSheet(sheetState) },
                onDone = { viewModel.takeAction(AddItemToPack) }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun AddItemsPageScreen(
        state: CreatePackViewModel.State,
        coroutineScope: CoroutineScope,
        pagerState: PagerState,
        router: Router
    ) {
        AddItemsScreen(
            name = state.nameFieldState.value.orEmpty(),
            onNextClicked = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(2)
                }
            },
            onNavigateBack = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            },
            canAddMoreItems = state.canAddMoreItems,
            onAddItemClicked = {
                router.navigateToEditPackItems(
                    packId = state.packId,
                    packItemName = null
                )
            },
            items = state.items,
            onEditItemClicked = {
                router.navigateToEditPackItems(
                    packId = state.packId,
                    packItemName = it.name
                )
            }
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun NamePackPageScreen(
        state: CreatePackViewModel.State,
        viewModel: CreatePackViewModel,
        coroutineScope: CoroutineScope,
        pagerState: PagerState,
        router: Router,
        sheetState: BottomSheetState
    ) {
        NamePackScreen(
            nameState = state.nameFieldState,
            onNameChanged = {
                viewModel.takeAction(UpdateName(it))
            },
            onNextClicked = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            },
            onNavigateBack = { router.dismissSheet(sheetState) }
        )
    }
}