package com.dangerfield.libraries.navigation

import androidx.navigation.NavGraphBuilder

/**
 * Class responsible for building a nav graph for a module/feature
 *
 * Each module should define a nav graph builder in its internal module with each screen in the module,
 * the routes to which should be exposed in the public module
 *
 * These builders should be bound into a set such that the app module can add all graphs to the
 * nav controller
 */
interface ModuleNavBuilder {

    fun NavGraphBuilder.buildNavGraph(router: Router)
}
