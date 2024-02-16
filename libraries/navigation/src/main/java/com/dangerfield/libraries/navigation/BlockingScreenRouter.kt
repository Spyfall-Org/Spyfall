package com.dangerfield.libraries.navigation

import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockingScreenRouter @Inject constructor() {

    val routes: Channel<Route.Filled> = Channel(Channel.UNLIMITED)

    fun goToBlockingScreen(route: Route.Filled) {
        routes.trySend(route)
    }
}