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
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.BuildInfo
import javax.inject.Inject

@AutoBindIntoSet
class SettingsModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo
): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = settingsNavigationRoute.navRoute,
            arguments = settingsNavigationRoute.navArguments,
        ) {
            SettingsScreen(
                versionName = buildInfo.versionName,
                isDebug = buildInfo.isDebug,
                onQaOptionClicked = router::navigateToQa,
                onNavigateBack = router::goBack,
                onThemeOptionClicked = router::navigateToColorPicker,
                onAboutOptionClicked = router::navigateToAbout,
                onContactUsClicked = router::navigateToContactUs
            )
        }

        composable(aboutRoute.navRoute) {
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
                onNameUpdated = {  viewModel.takeAction(UpdateName(it))  },
                onEmailUpdated = {  viewModel.takeAction(UpdateEmail(it))  },
                onMessageUpdated = {  viewModel.takeAction(UpdateMessage(it))  },
                wasFormSubmittedSuccessfully = state.wasFormSuccessfullySubmitted,
                didSomethingGoWrong = state.didSomethingGoWrong,
                onSomethingWentWrongDismissed = {  viewModel.takeAction(DismissSomethingWentWrong)  },
            )
        }
    }
}
