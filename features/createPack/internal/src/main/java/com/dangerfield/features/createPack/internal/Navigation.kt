package com.dangerfield.features.createPack.internal

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

val packIdArgument = navArgument("packId") { type = NavType.StringType }

val packItemNameArgument = navArgument("packItemId") {
    type = NavType.StringType
    nullable = true
}

val editPackItemRoute = route("editPackItemRoute") {
    argument(packIdArgument)
    argument(packItemNameArgument)
}

fun Router.navigateToEditPackItems(
    packId: String,
    packItemName: String?
) {
    navigate(
        fillRoute(editPackItemRoute) {
            fill(packItemNameArgument, packItemName)
            fill(packIdArgument, packId)
        }
    )
}