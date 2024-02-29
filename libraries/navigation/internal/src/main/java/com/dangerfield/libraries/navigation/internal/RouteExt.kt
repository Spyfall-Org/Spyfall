package com.dangerfield.libraries.navigation.internal

import androidx.navigation.NavOptions
import com.dangerfield.libraries.navigation.Route

fun Route.Filled.navOptions(): NavOptions {
    val builder = NavOptions.Builder()
    this.popUpTo?.let {
        builder.setPopUpTo(it.popUpToRoute.navRoute, it.popUpToInclusive)
    }

    this.restoreState?.let {
        builder.setRestoreState(it)
    }

    this.navAnimBuilder?.let {
        builder.setEnterAnim(it.enter)
        builder.setExitAnim(it.exit)
        builder.setPopEnterAnim(it.popEnter)
        builder.setPopExitAnim(it.popExit)
    }

    this.isLaunchSingleTop?.let {
        builder.setLaunchSingleTop(it)
    }

    return builder.build()
}

