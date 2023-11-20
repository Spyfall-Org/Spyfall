package com.dangerfield.features.blockingerror

import androidx.navigation.NavController

const val blockingErrorBaseRoute = "blockingError"

fun NavController.navigateToBlockingError() {
    this.navigate(blockingErrorBaseRoute)
}
