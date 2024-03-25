package com.dangerfield.features.colorpicker.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import com.dangerfield.features.colorpicker.colorPickerRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.dialog
import com.dangerfield.libraries.session.ColorConfig.Random
import com.dangerfield.libraries.session.ColorConfig.Specific
import com.dangerfield.libraries.ui.color.ThemeColor
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ColorPickerFeatureNavGraphBuilder @Inject constructor() : FeatureNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        dialog(
            route = colorPickerRoute.navRoute,
            arguments = colorPickerRoute.navArguments
        ) {

            val viewModel = hiltViewModel<ColorPickerViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val colorConfigs = ThemeColor.entries.map { Specific(it) } + Random

            PageLogEffect(
                route = colorPickerRoute,
                type = PageType.Dialog
            )

            ColorPickerDialog(
                onColorConfigSelected = {
                    viewModel.takeAction(ColorPickerViewModel.Action.UpdateColorConfig(it))
                },
                selectedColorConfig = state.colorConfig,
                onDismiss = router::goBack,
                colorConfigs = colorConfigs,
            )
        }
    }
}