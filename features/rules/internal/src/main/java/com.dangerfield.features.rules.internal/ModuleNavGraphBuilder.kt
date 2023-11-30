package com.dangerfield.features.rules.internal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.rules.rulesNavigationRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = rulesNavigationRoute,
        ) {
            RulesScreen()
        }
    }
}