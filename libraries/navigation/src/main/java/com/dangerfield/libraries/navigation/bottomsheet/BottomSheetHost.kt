package com.dangerfield.libraries.navigation.bottomsheet

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.LocalOwnersProvider
import com.dangerfield.libraries.navigation.slideDownToExitBottomSheet
import com.dangerfield.libraries.navigation.slideUpToEnterBottomSheet

/**
 * Host used to show bottom sheets that are added to the back stack.
 * This is what allows for bottom sheets to be their own destinations in a nav
 * graph rather than being owned by the showing composable.
 *
 * Shows each [Destination] on the [BottomSheetNavigator]'s back stack.
 *
 * Note that [SpyfallApp] should be the only called of this function.
 */
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun BottomSheetHost(bottomSheetNavigator: BottomSheetNavigator) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val bottomSheetBackstack by bottomSheetNavigator.backStack.collectAsState()
    val visibleBackStack = rememberVisibleList(bottomSheetBackstack)
    visibleBackStack.PopulateVisibleList(bottomSheetBackstack)

    visibleBackStack.forEach { backStackEntry ->

        val destination = backStackEntry.destination as BottomSheetNavigator.Destination

        val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
            {
                // if the bottom sheet is entering because another one was popped, dont animated
                if (bottomSheetNavigator.isPop.value) {
                    EnterTransition.None
                } else {
                    destination.enterTransition?.invoke(this) ?: slideUpToEnterBottomSheet()
                }
            }

        val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
            {
                // if the bottom sheet is exiting because its being popped, animate
                if (bottomSheetNavigator.isPop.value) {
                    destination.exitTransition?.invoke(this) ?: slideDownToExitBottomSheet()
                } else {
                    // exit is trigger by another destination being pushed on top
                    ExitTransition.None
                }
            }

        val transition = updateTransition(backStackEntry, label = "entry")
        transition.AnimatedContent(
            transitionSpec = {
                // Calculate a zIndex for each bottom sheet based on its position in the visibleBackStack.
                // The latest (topmost) bottom sheet should have the highest zIndex.
                // if for some reason the backStackEntry is not in the visibleBackStack, default to -1
                val zIndex = visibleBackStack.indexOf(backStackEntry).toFloat()
                ContentTransform(
                    enterTransition.invoke(this),
                    exitTransition.invoke(this),
                    targetContentZIndex = zIndex
                )
            },
            contentKey = { backStackEntry.id }
        ) {
            // while in the scope of the composable, we provide the navBackStackEntry as the
            // ViewModelStoreOwner and LifecycleOwner
            backStackEntry.LocalOwnersProvider(saveableStateHolder) {
                destination.content(this@AnimatedContent, backStackEntry)
            }
        }

        LaunchedEffect(transition.currentState, transition.targetState) {
            if (transition.currentState == transition.targetState) {
                bottomSheetNavigator.onTransitionComplete(backStackEntry)
            }
        }
    }
}

/**
 * @param transitionsInProgress is the current back stack entries (visible or not)
 *
 * Operation applied to the visible back stack entries.
 *
 *
 *
 *
 */
@Composable
internal fun MutableList<NavBackStackEntry>.PopulateVisibleList(
    transitionsInProgress: Collection<NavBackStackEntry>
) {
    val isInspecting = LocalInspectionMode.current
    transitionsInProgress.forEach { entry ->
        DisposableEffect(entry.lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                // for every lifecycle event, make sure the dialog is visible in preview
                if (isInspecting && !contains(entry)) {
                    add(entry)
                }
                // ON_START -> add to visibleBackStack, ON_STOP -> remove from visibleBackStack
                if (event == Lifecycle.Event.ON_START) {
                    // We want to treat the visible lists as Sets but we want to keep
                    // the functionality of mutableStateListOf() so that we recompose in response
                    // to adds and removes.
                    if (!contains(entry)) {
                        add(entry)
                    }
                }
                if (event == Lifecycle.Event.ON_STOP) {
                    remove(entry)
                }
            }
            entry.lifecycle.addObserver(observer)
            onDispose {
                entry.lifecycle.removeObserver(observer)
            }
        }
    }
}

/**
 * Takes in the state of the back stack and returns a list of the entries that are visible
 * (i.e. have a lifecycle state of at least STARTED).
 *
 * For preview, all entries are considered visible.
 */
@Composable
internal fun rememberVisibleList(
    transitionsInProgress: Collection<NavBackStackEntry>
): SnapshotStateList<NavBackStackEntry> {
    // show dialog in preview
    val isInspecting = LocalInspectionMode.current
    return remember(transitionsInProgress) {
        mutableStateListOf<NavBackStackEntry>().also {
            it.addAll(
                transitionsInProgress.filter { entry ->
                    if (isInspecting) {
                        true
                    } else {
                        entry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
                    }
                }
            )
        }
    }
}
