package com.dangerfield.features.blockingerror

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

val blockingErrorRoute = route("blocking_error")

val maintenanceRoute = route("maintenance")

fun Router.navigateToBlockingError() {
    this.navigate(blockingErrorRoute.noArgRoute())
}

val errorClassArgument = navArgument("error_class") {
    type = NavType.StringType
    nullable = true
}

val generalErrorDialog = route("general_error_dialog") {
    argument(errorClassArgument)
}

fun Router.navigateToGeneralErrorDialog(
    errorClass: String? = null,
) {
    this.navigate(
        fillRoute(generalErrorDialog) {
            fill(errorClassArgument, errorClass)
        }
    )
}
