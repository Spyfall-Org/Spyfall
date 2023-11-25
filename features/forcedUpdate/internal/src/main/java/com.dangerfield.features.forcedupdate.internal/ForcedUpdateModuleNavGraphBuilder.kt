package com.dangerfield.features.forcedupdate.internal

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.BuildInfo
import spyfallx.core.openStoreLinkToApp
import spyfallx.ui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class ForcedUpdateModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo
): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = forcedUpdateNavigationRoute,
        ) {
            val context = LocalContext.current
            ForcedUpdateScreen(
                onOpenAppStoreClicked = {
                    context.openStoreLinkToApp(buildInfo)
                }
            )
        }
    }
}
