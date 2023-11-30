package com.dangerfield.features.qa.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.qa.qaNavigationRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class QaModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = qaNavigationRoute,
        ) {
            val viewModel: QaViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            QaScreen(
                configuredValues = state.configValues,
                experiments = state.experiments,
                onExperimentOverride = { experiment, value ->
                    viewModel.addOverride(
                        path = experiment.path,
                        value = value
                    )
                },
                onNavigateBack = navController::popBackStack
            )
        }
    }
}
