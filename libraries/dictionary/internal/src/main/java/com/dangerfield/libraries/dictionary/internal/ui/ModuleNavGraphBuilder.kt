package com.dangerfield.libraries.dictionary.internal.ui

import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.LanguageSupportLevel
import com.dangerfield.libraries.dictionary.supportLevelNameMap
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.dialog
import com.dangerfield.libraries.navigation.navArgument
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        dialog(
            route = languageSupportRoute.navRoute,
            arguments = languageSupportRoute.navArguments
        ) {

            val language = it.navArgument<String>(languageNavArgument) ?: return@dialog
            val supportLevelName = it.navArgument<String>(supportLevelNameNavArgument) ?: return@dialog
            val supportLevelClazz = supportLevelNameMap[supportLevelName]

            val isUnsupported = when (supportLevelClazz) {
                LanguageSupportLevel.NotSupported::class -> true
                else -> false
            }

            PageLogEffect(
                route = languageSupportRoute,
                type = PageType.Dialog,
                extras = bundleOf(
                    "language" to language,
                    "supportLevelName" to supportLevelName
                )
            )

            LanguageSupportDialog(
                language = language,
                isUnsupported = isUnsupported,
                onDismissRequest = router::goBack
            )
        }
    }
}