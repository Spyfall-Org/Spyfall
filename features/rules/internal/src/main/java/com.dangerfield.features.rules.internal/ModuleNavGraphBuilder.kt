package com.dangerfield.features.rules.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.rules.rulesNavigationRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.bottomsheet.bottomSheet
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        bottomSheet(
            route = rulesNavigationRoute.navRoute,
            arguments = rulesNavigationRoute.navArguments
        ) {
            RulesBottomSheet(
                onDismissRequest = router::dismissSheet
            )
        }
    }
}