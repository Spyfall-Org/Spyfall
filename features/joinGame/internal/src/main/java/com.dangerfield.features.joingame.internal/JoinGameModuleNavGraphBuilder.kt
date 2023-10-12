package com.dangerfield.features.joingame.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.joingame.joinGameNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.coreui.ModuleNavBuilder
import spyfallx.coreui.components.text.Text
import javax.inject.Inject

@AutoBindIntoSet
class JoinGameModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = joinGameNavigationRoute,
        ) {
            Column(Modifier.fillMaxSize()) {
                Text(text = "Join Game")
            }
        }
    }
}