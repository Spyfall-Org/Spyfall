package com.dangerfield.libraries.ui.components.dialog.bottomsheet

import androidx.compose.material3.SheetValue as MaterialSheetValue

enum class BottomSheetValue(internal val materialValue: MaterialSheetValue) {
    Hidden(MaterialSheetValue.Hidden),
    Expanded(MaterialSheetValue.Expanded),
    PartiallyExpanded(MaterialSheetValue.PartiallyExpanded)
}

internal fun MaterialSheetValue.toBottomSheetValue(): BottomSheetValue =
    when (this) {
        MaterialSheetValue.Hidden -> BottomSheetValue.Hidden
        MaterialSheetValue.Expanded -> BottomSheetValue.Expanded
        MaterialSheetValue.PartiallyExpanded -> BottomSheetValue.PartiallyExpanded
    }
