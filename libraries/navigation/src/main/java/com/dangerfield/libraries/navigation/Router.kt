package com.dangerfield.libraries.navigation


interface Router {
    fun navigate(filledRoute: Route.Filled)

    fun goBack()
}