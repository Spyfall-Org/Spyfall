package com.dangerfield.features.qa.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.qa.qaNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.LocalBuildInfo
import oddoneout.core.BuildType
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class QaModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = qaNavigationRoute.navRoute,
            arguments = qaNavigationRoute.navArguments
        ) {

            val buildInfo = LocalBuildInfo.current

            if (buildInfo.buildType !in listOf(BuildType.DEBUG, BuildType.DEBUG)) {
                router.goBack()
                return@composable
            }

            val viewModel: QaViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            PageLogEffect(
                route = qaNavigationRoute,
                type = PageType.FullScreenPage
            )

            QaScreen(
                configuredValues = state.configValues,
                experiments = state.experiments,
                onExperimentOverride = { experiment, value ->
                    viewModel.addOverride(
                        path = experiment.path,
                        value = value
                    )
                },
                onConfigValueOverride = { configValue, value ->
                    viewModel.addOverride(
                        path = configValue.path,
                        value = value
                    )
                },
                onNavigateBack = router::goBack,
                sessionId = state.sessionId
            )
        }
    }
}
