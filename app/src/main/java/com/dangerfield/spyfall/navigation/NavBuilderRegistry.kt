package com.dangerfield.spyfall.navigation

import androidx.navigation.NavGraphBuilder
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import javax.inject.Inject

// TODO cleanup feels like this should be in the nav library
class NavBuilderRegistry @Inject constructor(
    private val navBuilders: Set<@JvmSuppressWildcards ModuleNavBuilder>,
) {

    fun registerNavBuilderForModule(navGraphBuilder: NavGraphBuilder, router: Router) {
        navBuilders.forEach { moduleNavBuilder ->
            with(moduleNavBuilder) {
                navGraphBuilder.buildNavGraph(router)
            }
        }
    }
}
