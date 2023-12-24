package com.dangerfield.spyfall.navigation

import android.util.Log
import androidx.navigation.NavGraphBuilder
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import javax.inject.Inject

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
