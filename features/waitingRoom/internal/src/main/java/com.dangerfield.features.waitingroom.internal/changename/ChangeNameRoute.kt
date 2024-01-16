package com.dangerfield.features.waitingroom.internal.changename

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToChangeName(
    accessCode: String,
) {
    navigate(
        fillRoute(changeNameRoute) {
            fill(changeNameAccessCodeArgument, accessCode)
        }
    )
}

val changeNameAccessCodeArgument = navArgument("accessCode") { type = NavType.StringType }

val changeNameRoute = route("change_name_dialog") {
    argument(changeNameAccessCodeArgument)
}
