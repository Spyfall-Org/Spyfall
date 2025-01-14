package com.dangerfield.features.settings.internal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.blockingerror.navigateToGeneralErrorDialog
import com.dangerfield.features.colorpicker.navigateToColorPicker
import com.dangerfield.features.consent.OpenGDRPConsentForm
import com.dangerfield.features.consent.ShouldShowGDRPSettingsOption
import com.dangerfield.features.qa.navigateToQa
import com.dangerfield.features.settings.internal.referral.ReferralViewModel.Action.Redeem
import com.dangerfield.features.settings.internal.referral.ReferralViewModel.Action.UpdateReferralCodeField
import com.dangerfield.features.settings.internal.contactus.ContactUsScreen
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.DismissSomethingWentWrong
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.Submit
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateContactReason
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateEmail
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateMessage
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action.UpdateName
import com.dangerfield.features.settings.internal.referral.IsReferralFeatureEnabled
import com.dangerfield.features.settings.internal.referral.ReferralScreen
import com.dangerfield.features.settings.internal.referral.ReferralViewModel
import com.dangerfield.features.settings.settingsNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.session.SessionFlow
import com.dangerfield.libraries.session.Stats
import com.dangerfield.oddoneoout.features.settings.internal.R
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import oddoneout.core.BuildInfo
import oddoneout.core.BuildType
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
@ActivityScoped
class SettingsFeatureNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo,
    private val sessionFlow: SessionFlow,
    private val dictionary: Dictionary,
    private val shouldShowGDRPSettingsOption: ShouldShowGDRPSettingsOption,
    private val openGDRPConsentForm: OpenGDRPConsentForm,
    private val isReferralFeatureEnabled: IsReferralFeatureEnabled
) : FeatureNavBuilder {

    @Suppress("LongMethod")
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
                versionName = "${buildInfo.versionName}(${buildInfo.versionCode})",
                isQaOptionEnabled = buildInfo.buildType in listOf(
                    BuildType.DEBUG,
                    BuildType.QA
                ),
                onQaOptionClicked = router::navigateToQa,
                onNavigateBack = router::goBack,
                onThemeOptionClicked = router::navigateToColorPicker,
                onAboutOptionClicked = router::navigateToAbout,
                onStatsClicked = router::navigateToStats,
                onContactUsClicked = router::navigateToContactUs,
                onReferralClicked = router::navigateToReferral,
                isReferralFeatureEnabled = remember { isReferralFeatureEnabled() }
            )
        }

        composable(aboutRoute.navRoute) {
            val scope = rememberCoroutineScope()

            PageLogEffect(
                route = aboutRoute,
                type = PageType.FullScreenPage
            )

            AboutScreen(
                versionName = "${buildInfo.versionName}(${buildInfo.versionCode})",
                onNavigateBack = router::goBack,
                shouldShowConsentFormOption = shouldShowGDRPSettingsOption(),
                onPrivacyPolicyClicked = { router.openWebLink(dictionary.getString(R.string.about_privacyPolicy_link)) },
                onTermsOfServiceClicked = { router.openWebLink(dictionary.getString(R.string.about_termsOfService_link)) },
                onThirdPartyServicesClicked = { router.openWebLink(dictionary.getString(R.string.about_thirdPartyServices_link)) },
                onManageConsentClicked = {
                    scope.launch {
                        openGDRPConsentForm(onlyIfNeeded = false)
                            .onFailure {
                                router.navigateToGeneralErrorDialog(it::class.java.name)
                            }
                    }
                },
            )
        }

        composable(contactUsRoute.navRoute) {

            val viewModel = hiltViewModel<ContactUsViewModel>()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()

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

        composable(referralCode.navRoute) {
            val viewModel = hiltViewModel<ReferralViewModel>()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()

            PageLogEffect(
                route = referralCode,
                type = PageType.FullScreenPage
            )

            ReferralScreen(
                meReferralCode = state.meReferralCode,
                meRedemptionStatus = state.meRedemptionStatus,
                referralCodeFieldState = state.referralCodeFieldState,
                featureMessage = state.featureMessage,
                isFormValid = state.isFormValid,
                maxRedemptions = state.maxRedemptions,
                isLoading = state.isLoading,
                onRedeemClicked = { viewModel.takeAction(Redeem(state.referralCodeFieldState.value.orEmpty())) },
                onNavigateBack = router::goBack,
                onReferralCodeFieldUpdated = { viewModel.takeAction(UpdateReferralCodeField(it)) }
            )
        }
    }
}
