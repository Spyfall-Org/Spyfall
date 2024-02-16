package com.dangerfield.features.consent.internal

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.needInHouseConsentRoute
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.oddoneoout.features.consent.internal.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
@ActivityScoped
class ModuleNavGraphBuilder @Inject constructor(
    private val inHouseConsentManager: InHouseConsentManager,
    @ActivityContext private val context: Context,
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = needInHouseConsentRoute.navRoute,
            arguments = needInHouseConsentRoute.navArguments
        ) {
            val dictionary = LocalDictionary.current
            val coroutineScope = rememberCoroutineScope()
            val activity = context as? Activity

            InHouseConsentScreen(
                onAcceptClicked = {
                    coroutineScope.launch {
                        activity?.let {
                            inHouseConsentManager.updateConsentStatus(ConsentStatus.ConsentGiven)
                        }
                    }
                },
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