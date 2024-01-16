package com.dangerfield.features.rules.internal

import androidx.navigation.NavGraphBuilder
import com.dangerfield.features.rules.rulesNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.bottomSheet
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        bottomSheet(
            route = rulesNavigationRoute.navRoute,
            arguments = rulesNavigationRoute.navArguments
        ) {

            PageLogEffect(
                route = rulesNavigationRoute,
                type = PageType.BottomSheet
            )

            RulesBottomSheet(
                onDismissRequest = router::dismissSheet
            )
        }
    }
}