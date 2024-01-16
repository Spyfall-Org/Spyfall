package com.dangerfield.features.settings.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.colorpicker.navigateToColorPicker
import com.dangerfield.features.qa.navigateToQa
import com.dangerfield.features.settings.internal.contactus.ContactUsScreen
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.DismissSomethingWentWrong
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.Submit
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateContactReason
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateEmail
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateMessage
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateName
import com.dangerfield.features.settings.settingsNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.session.SessionFlow
import com.dangerfield.libraries.session.Stats
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBindIntoSet
import oddoneout.core.BuildInfo
import javax.inject.Inject

@AutoBindIntoSet
class SettingsModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo,
    private val sessionFlow: SessionFlow
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = settingsNavigationRoute.navRoute,
            arguments = settingsNavigationRoute.navArguments,
        ) {

            PageLogEffect(
                route = settingsNavigationRoute,
                type = PageType.FullScreenPage
            )

            SettingsScreen(
                versionName = buildInfo.versionName,
                isDebug = buildInfo.isDebug,
                onQaOptionClicked = router::navigateToQa,
                onNavigateBack = router::goBack,
                onThemeOptionClicked = router::navigateToColorPicker,
                onAboutOptionClicked = router::navigateToAbout,
                onStatsClicked = router::navigateToStats,
                onContactUsClicked = router::navigateToContactUs
            )
        }

        composable(aboutRoute.navRoute) {
            PageLogEffect(
                route = aboutRoute,
                type = PageType.FullScreenPage
            )

            AboutScreen(
                versionName = buildInfo.versionName,
                onNavigateBack = router::goBack,
                onPrivacyPolicyClicked = { router.openWebLink("https://spyfall-org.github.io/") },
                onTermsOfServiceClicked = { router.openWebLink("https://spyfall-org.github.io/") },
                onThirdPartyServicesClicked = { router.openWebLink("https://spyfall-org.github.io/") }
            )
        }

        composable(contactUsRoute.navRoute) {

            val viewModel = hiltViewModel<ContactUsViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            PageLogEffect(
                route = contactUsRoute,
                type = PageType.FullScreenPage
            )

            ContactUsScreen(
                isLoadingSubmit = state.isLoadingSubmit,
                isFormValid = state.isFormValid,
                nameFieldState = state.nameFieldState,
                emailFieldState = state.emailFieldState,
                messageFieldState = state.messageFieldState,
                onNavigateBack = router::goBack,
                onSubmitClicked = { viewModel.takeAction(Submit) },
                contactReasonFieldState = state.contactReasonState,
                onContactReasonSelected = { viewModel.takeAction(UpdateContactReason(it)) },
                onNameUpdated = { viewModel.takeAction(UpdateName(it)) },
                onEmailUpdated = { viewModel.takeAction(UpdateEmail(it)) },
                onMessageUpdated = { viewModel.takeAction(UpdateMessage(it)) },
                wasFormSubmittedSuccessfully = state.wasFormSuccessfullySubmitted,
                didSubmitFail = state.didSubmitFail,
                onSomethingWentWrongDismissed = { viewModel.takeAction(DismissSomethingWentWrong) },
            )
        }

        composable(stats.navRoute) {
            val statsState by sessionFlow
                .map { it.user.stats }
                .collectAsStateWithLifecycle(
                    initialValue = Stats(
                        multiDeviceGamesPlayed = 0,
                        winsAsOddOne = listOf(),
                        winsAsPlayer = listOf(),
                        lossesAsOddOne = listOf(),
                        lossesAsPlayer = listOf(),
                        singleDeviceGamesPlayed = 0
                    )
                )

            PageLogEffect(
                route = stats,
                type = PageType.FullScreenPage
            )

            StatsScreen(
                onNavigateBack = router::goBack,
                gamesLostAsPlayer = statsState.lossesAsPlayer.count(),
                gamesLostAsOddOne = statsState.lossesAsOddOne.count(),
                gamesWonAsPlayer = statsState.winsAsPlayer.count(),
                gamesWonAsOddOne = statsState.winsAsOddOne.count(),
                totalMultiDeviceGamesPlayed = statsState.multiDeviceGamesPlayed,
                totalSingleDeviceGamesPlayed = statsState.singleDeviceGamesPlayed,
            )
        }
    }
}
