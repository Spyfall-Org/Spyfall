package com.dangerfield.features.forcedupdate.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.coreui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class ForcedUpdateModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {
    override fun NavGraphBuilder.buildNavGraph() {
        composable(
            route = forcedUpdateNavigationRoute,
        ) {
            ForcedUpdateScreen()
        }
    }
}