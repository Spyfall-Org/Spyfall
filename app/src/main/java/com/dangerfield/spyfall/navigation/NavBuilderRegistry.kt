package com.dangerfield.spyfall.navigation

import androidx.navigation.NavGraphBuilder
import spyfallx.coreui.ModuleNavBuilder
import javax.inject.Inject

class NavBuilderRegistry @Inject constructor(
    private val navBuilders: Set<@JvmSuppressWildcards ModuleNavBuilder>,
) {

    fun registerNavBuilderForModule(navGraphBuilder: NavGraphBuilder) {
        navBuilders.forEach { moduleNavBuilder ->
            with(moduleNavBuilder) {
                navGraphBuilder.buildNavGraph()
            }
        }
    }
}