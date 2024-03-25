package com.dangerfield.features.consent.internal.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.consent.OpenGDRPConsentForm
import com.dangerfield.features.consent.consentRoute
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.oddoneoout.features.consent.internal.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import oddoneout.core.eitherWay
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
@ActivityScoped
class FeatureNavGraphBuilder @Inject constructor(
    @ActivityContext private val context: Context,
    private val openGDRPConsentForm: OpenGDRPConsentForm
) : FeatureNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = consentRoute.navRoute,
            arguments = consentRoute.navArguments
        ) {
            val dictionary = LocalDictionary.current
            val viewModel = hiltViewModel<ConsentViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val activity = context as Activity
            var isLoadingGDRPConsent by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                viewModel.loadConsentStatus(activity)
            }

            LaunchedEffect(state.shouldShowGDRPConsentMessage) {
                if (state.shouldShowGDRPConsentMessage) {
                    isLoadingGDRPConsent = true
                    openGDRPConsentForm(onlyIfNeeded = true)
                        .logOnFailure()
                        .onFailure {
                            viewModel.forceGDRPToBeIgnored(activity)
                        }
                        .eitherWay { isLoadingGDRPConsent = false }
                }
            }

            when {
                state.shouldShowInHouseConsentMessage -> {
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
                isLoadingGDRPConsent -> ConsentLoadingScreen()
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}