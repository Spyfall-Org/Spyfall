package com.dangerfield.features.qa.internal

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.qa.qaNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.ui.LocalBuildInfo
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import oddoneout.core.BuildType
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
@ActivityScoped
class QaFeatureNavGraphBuilder @Inject constructor(
    @ActivityContext private val context: Context,
    private val session: Session,
) : FeatureNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = qaNavigationRoute.navRoute,
            arguments = qaNavigationRoute.navArguments
        ) {

            val buildInfo = LocalBuildInfo.current

            if (buildInfo.buildType !in listOf(BuildType.DEBUG, BuildType.QA)) {
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
                sessionId = state.sessionId,
                consentExperiments = state.consentExperiments,
                languageCode = session.user.languageCode,
                onResetConsent = { viewModel.resetConsent(context as Activity) }
            )
        }
    }
}
