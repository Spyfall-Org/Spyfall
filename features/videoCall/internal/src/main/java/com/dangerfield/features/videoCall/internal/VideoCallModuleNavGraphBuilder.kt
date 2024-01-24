package com.dangerfield.features.videoCall.internal

import androidx.navigation.NavGraphBuilder
import com.dangerfield.features.videoCall.videoCallLinkInfoRoute
import com.dangerfield.features.videoCall.videoCallDetails
import com.dangerfield.features.videoCall.videoLinkArgument
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
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
            route = videoCallDetails.navRoute,
            arguments = videoCallDetails.navArguments
        ) {
            val link = it.navArgument<String>(videoLinkArgument) ?: return@bottomSheet

            PageLogEffect(
                route = videoCallDetails,
                type = PageType.BottomSheet
            )

            VideoLinkBottomSheet(
                link = link,
                onVideoLinkClicked = {
                    router.dismissSheet(it)
                    router.openWebLink(link, openInApp = false)
                },
                onDismiss = router::dismissSheet
            )
        }

        bottomSheet(
            route = videoCallLinkInfoRoute.navRoute,
            arguments = videoCallLinkInfoRoute.navArguments
        ) {

            PageLogEffect(route = videoCallLinkInfoRoute,
                type = PageType.BottomSheet
            )

            VideoCallInfoBottomSheet(
                recognizedPlatforms = recognizedVideoCallingPlatforms().keys.toList(),
                onDismiss = router::dismissSheet,
            )
        }
    }
}