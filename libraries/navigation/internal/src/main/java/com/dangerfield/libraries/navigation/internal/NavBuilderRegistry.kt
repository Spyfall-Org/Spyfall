package com.dangerfield.libraries.navigation.internal

import androidx.navigation.NavGraphBuilder
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import javax.inject.Inject

class NavBuilderRegistry @Inject constructor(
    private val navBuilders: Set<@JvmSuppressWildcards ModuleNavBuilder>,
    private val router: Router
) {

    fun registerNavBuilderForModule(navGraphBuilder: NavGraphBuilder) {
        navBuilders.forEach { moduleNavBuilder ->
            with(moduleNavBuilder) {
                navGraphBuilder.buildNavGraph(router)
            }
        }
    }
}
