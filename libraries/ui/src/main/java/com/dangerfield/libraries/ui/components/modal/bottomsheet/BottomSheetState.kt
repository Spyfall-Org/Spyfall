package com.dangerfield.libraries.ui.components.modal.bottomsheet

import android.annotation.SuppressLint
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CancellationException

@Stable
class BottomSheetState(
    initialValue: BottomSheetValue,
    internal var confirmValueChange: (BottomSheetValue) -> Boolean = { true },
    skipPartiallyExpanded: Boolean = true,
    skipHiddenState: Boolean = false
) {
    internal val sheetState = SheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        initialValue = initialValue.value,
        confirmValueChange = { confirmValueChange(it.toBottomSheetValue()) },
        skipHiddenState = skipHiddenState
    )

    val currentValue: BottomSheetValue
        get() = sheetState.currentValue.toBottomSheetValue()

    val targetValue: BottomSheetValue
        get() = sheetState.targetValue.toBottomSheetValue()

    val isVisible: Boolean
        get() = sheetState.isVisible

    /**
     * Show the bottom sheet with animation and suspend until it's shown. If the sheet is taller
     * than 50% of the parent's height, the bottom sheet will be half expanded. Otherwise it will be
     * fully expanded.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun show() = sheetState.show()

    /**
     * Hide the bottom sheet with animation and suspend until it if fully hidden or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun hide() = sheetState.hide()

    /**
     * Fully expand the bottom sheet with animation and suspend until it is fully expanded or
     * animation has been cancelled.
     * *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun expand() = sheetState.expand()

    /**
     * Animate the bottom sheet and suspend until it is partially expanded or animation has been
     * cancelled.
     * @throws [CancellationException] if the animation is interrupted
     * @throws [IllegalStateException] if [skipPartiallyExpanded] is set to true
     */
    suspend fun partialExpand() = sheetState.partialExpand()

    companion object {
        @Suppress("FunctionNaming")
        @SuppressLint("ComposableNaming")
        fun Saver(confirmValueChange: (BottomSheetValue) -> Boolean): Saver<BottomSheetState, *> =
            Saver(
                save = { it.currentValue },
                restore = { BottomSheetState(it, confirmValueChange) }
            )
    }
}

@Composable
fun rememberBottomSheetState(
    initialState: BottomSheetValue = BottomSheetValue.Hidden,
    confirmValueChange: (BottomSheetValue) -> Boolean = { true },
    skipPartiallyExpanded: Boolean = initialState != BottomSheetValue.PartiallyExpanded,
    skipHiddenState: Boolean = false
) = rememberSaveable(saver = BottomSheetState.Saver(confirmValueChange)) {
    BottomSheetState(
        initialState,
        confirmValueChange,
        skipPartiallyExpanded = skipPartiallyExpanded,
        skipHiddenState = skipHiddenState
    )
}.also { it.confirmValueChange = confirmValueChange }
