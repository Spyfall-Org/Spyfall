package com.dangerfield.features.colorpicker.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import com.dangerfield.features.colorpicker.colorPickerRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.dialog
import com.dangerfield.libraries.session.ColorConfig.Random
import com.dangerfield.libraries.session.ColorConfig.Specific
import com.dangerfield.libraries.ui.color.ThemeColor
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ColorPickerModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        dialog(
            route = colorPickerRoute.navRoute,
            arguments = colorPickerRoute.navArguments
        ) {

            val viewModel = hiltViewModel<ColorPickerViewModel>()

            val state by viewModel.state.collectAsStateWithLifecycle()

            val colorConfigs = ThemeColor.entries.map { Specific(it) } + Random

            ColorPickerDialog(
                onConfigSelected = {
                    viewModel.takeAction(ColorPickerViewModel.Action.UpdateColorConfig(it))
                },
                selectedConfig = state.colorConfig,
                onDismiss = router::goBack,
                colorConfigs = colorConfigs
            )
        }
    }
}