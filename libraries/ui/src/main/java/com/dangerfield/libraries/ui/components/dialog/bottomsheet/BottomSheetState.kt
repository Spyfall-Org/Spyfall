package com.dangerfield.libraries.ui.components.dialog.bottomsheet

import android.annotation.SuppressLint
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Stable
class BottomSheetState(
    initialValue: BottomSheetValue,
    density: Density,
    internal var confirmValueChange: (BottomSheetValue) -> Boolean = { true },
    skipPartiallyExpanded: Boolean = true,
    skipHiddenState: Boolean = false,
) {
    internal val materialSheetStateDelegate = SheetState(
        density = density,
        skipPartiallyExpanded = skipPartiallyExpanded,
        initialValue = initialValue.materialValue,
        confirmValueChange = { confirmValueChange(it.toBottomSheetValue()) },
        skipHiddenState = skipHiddenState
    )

    val currentValue: BottomSheetValue
        get() = materialSheetStateDelegate.currentValue.toBottomSheetValue()

    val targetValue: BottomSheetValue
        get() = materialSheetStateDelegate.targetValue.toBottomSheetValue()

    val isVisible: Boolean
        get() = materialSheetStateDelegate.isVisible

    suspend fun show() = materialSheetStateDelegate.show()
    suspend fun hide() = materialSheetStateDelegate.hide()

    companion object {
        @Suppress("FunctionNaming")
        @SuppressLint("ComposableNaming")
        fun Saver(confirmValueChange: (BottomSheetValue) -> Boolean, density: Density): Saver<BottomSheetState, *> =
            Saver(
                save = { it.currentValue },
                restore = { BottomSheetState(it, density,  confirmValueChange, ) }
            )
    }
}

@Composable
fun rememberBottomSheetState(
    initialState: BottomSheetValue = BottomSheetValue.Hidden,
    confirmValueChange: (BottomSheetValue) -> Boolean = { true },
    skipPartiallyExpanded: Boolean = initialState != BottomSheetValue.PartiallyExpanded,
    skipHiddenState: Boolean = false,
    density: Density = LocalDensity.current
) = rememberSaveable(saver = BottomSheetState.Saver(confirmValueChange, density)) {
    BottomSheetState(
        initialState,
        density,
        confirmValueChange,
        skipPartiallyExpanded = skipPartiallyExpanded,
        skipHiddenState = skipHiddenState
    )
}
