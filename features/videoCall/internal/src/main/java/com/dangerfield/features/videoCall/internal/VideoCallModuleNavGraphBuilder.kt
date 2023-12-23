package com.dangerfield.features.videoCall.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.videoCall.videoCallLinkInfoRoute
import com.dangerfield.features.videoCall.videoCallRoute
import com.dangerfield.features.videoCall.videoLinkArgument
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.bottomSheet
import com.dangerfield.libraries.navigation.navArgument
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class VideoCallModuleNavGraphBuilder @Inject constructor(
    private val recognizedVideoCallingPlatforms: RecognizedVideoCallingPlatforms
): ModuleNavBuilder {
    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        bottomSheet(
            route = videoCallRoute.navRoute,
            arguments = videoCallRoute.navArguments
        ) {
            val link = it.navArgument<String>(videoLinkArgument) ?: return@bottomSheet

            VideoLinkBottomSheet(
                link = link,
                onDismiss = router::dismissSheet
            )
        }

        bottomSheet(
            route = videoCallLinkInfoRoute.navRoute,
            arguments = videoCallLinkInfoRoute.navArguments
        ) {
            VideoCallInfoBottomSheet(
                recognizedPlatforms = recognizedVideoCallingPlatforms().keys.toList(),
                onDismiss = router::dismissSheet,
            )
        }
    }
}