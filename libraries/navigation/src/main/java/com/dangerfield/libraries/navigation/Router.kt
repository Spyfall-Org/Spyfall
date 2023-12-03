package com.dangerfield.libraries.navigation

import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState


interface Router {
    fun navigate(filledRoute: Route.Filled)
    fun goBack()
    fun dismissSheet(sheetState: BottomSheetState)
}