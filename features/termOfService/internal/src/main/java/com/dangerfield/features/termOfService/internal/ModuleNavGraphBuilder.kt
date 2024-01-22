package com.dangerfield.features.termOfService.internal

import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.termOfService.LegalAcceptanceState
import com.dangerfield.features.termOfService.termOfServiceRoute
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.oddoneoout.features.termofservice.internal.R
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(
    private val legalAcceptanceRepository: LegalAcceptanceRepository
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = termOfServiceRoute.navRoute,
            arguments = termOfServiceRoute.navArguments
        ) {
            val dictionary = LocalDictionary.current
            val coroutineScope = rememberCoroutineScope()

            TermsOfServiceScreen(
                onAcceptClicked = {
                    coroutineScope.launch {
                        legalAcceptanceRepository.updateAcceptanceState(LegalAcceptanceState.Accepted)
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