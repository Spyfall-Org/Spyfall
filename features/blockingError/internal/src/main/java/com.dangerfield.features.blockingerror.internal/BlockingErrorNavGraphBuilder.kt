package com.dangerfield.features.blockingerror.internal

import android.app.Activity
import android.content.Context
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.dangerfield.features.blockingerror.blockingErrorRoute
import com.dangerfield.features.blockingerror.errorClassArgument
import com.dangerfield.features.blockingerror.generalErrorDialog
import com.dangerfield.features.blockingerror.maintenanceRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.navArgument
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject


@AutoBindIntoSet
@ActivityScoped
class BlockingErrorNavGraphBuilder @Inject constructor(
    @ActivityContext private val context: Context,
) : FeatureNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = blockingErrorRoute.navRoute,
            arguments = blockingErrorRoute.navArguments
        ) {

            PageLogEffect(
                route = blockingErrorRoute,
                type = PageType.FullScreenPage
            )

            BlockingErrorScreen(
                onBackPressed = {
                    (context as? Activity)?.finish()
                }
            )
        }

        composable(
            route = maintenanceRoute.navRoute,
            arguments = maintenanceRoute.navArguments
        ) {

            PageLogEffect(
                route = maintenanceRoute,
                type = PageType.FullScreenPage
            )

            MaintenanceModeScreen()
        }

        dialog(
            route = generalErrorDialog.navRoute,
            arguments = generalErrorDialog.navArguments
        ) {

            val errorClass = it.navArgument<String>(errorClassArgument)

            GeneralErrorDialog(
                onDismiss = router::goBack,
                errorClass = errorClass
            )
        }
    }
}
