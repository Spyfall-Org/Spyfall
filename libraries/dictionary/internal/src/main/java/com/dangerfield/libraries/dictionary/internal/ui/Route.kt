package com.dangerfield.libraries.dictionary.internal.ui

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToLanguageSupportDialog(supportLevelName: String, languageDisplayName: String) {
    navigate(
        fillRoute(languageSupportRoute) {
            fill(supportLevelNameNavArgument, supportLevelName)
            fill(languageNavArgument, languageDisplayName)
        }
    )
}

val supportLevelNameNavArgument = navArgument("support_level") {
    type = NavType.StringType
}

val languageNavArgument = navArgument("language") {
    type = NavType.StringType
}

val languageSupportRoute = route("language_support_dialog") {
    argument(supportLevelNameNavArgument)
    argument(languageNavArgument)
}