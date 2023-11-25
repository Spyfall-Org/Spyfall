package spyfallx.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder


/**
 * Implementations of the [ModuleNavBuilder] live in the same feature modules where the destinations (ex. Fragments) are defined.
 * All the [ModuleNavBuilder] will be registered during App Start up, and build the graph in AppActivity onCreate.
 * See one of the Implementation of [ModuleNavBuilder] for example.
 *
 */
interface ModuleNavBuilder {

    fun NavGraphBuilder.buildNavGraph(navController: NavController)
}
