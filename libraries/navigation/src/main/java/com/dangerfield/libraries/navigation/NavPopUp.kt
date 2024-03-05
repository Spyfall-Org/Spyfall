package com.dangerfield.libraries.navigation

data class NavPopUp(
    val popUpToRoute: Route.Template,
    val popUpToInclusive: Boolean = false,
)