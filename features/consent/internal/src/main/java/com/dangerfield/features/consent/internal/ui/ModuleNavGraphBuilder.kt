package com.dangerfield.features.consent.internal.ui

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.consent.consentRoute
import com.dangerfield.features.consent.internal.ui.ConsentViewModel
import com.dangerfield.features.consent.internal.ui.InHouseConsentScreen
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.oddoneoout.features.consent.internal.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
@ActivityScoped
class ModuleNavGraphBuilder @Inject constructor(
    @ActivityContext private val context: Context,
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = consentRoute.navRoute,
            arguments = consentRoute.navArguments
        ) {
            val dictionary = LocalDictionary.current
            val viewModel = hiltViewModel<ConsentViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val activity = context as Activity

            LaunchedEffect(Unit) {
                viewModel.loadConsentStatus(activity)
            }

            LaunchedEffect(state.shouldShowGDRPConsentMessage) {
                if (state.shouldShowGDRPConsentMessage) {
                    viewModel.openGDRPConsentForm(context )
                }
            }

            if (state.shouldShowInHouseConsentMessage) {
                InHouseConsentScreen(
                    onAcceptClicked = viewModel::onInHouseConsentGiven,
                    onTermsOfServiceClicked = {
                        router.openWebLink(dictionary.getString(R.string.about_termsOfService_link))
                    },
                    onPrivacyPolicyClicked = {
                        router.openWebLink(dictionary.getString(R.string.about_privacyPolicy_link))
                    }
                )
            }
        }
    }
}