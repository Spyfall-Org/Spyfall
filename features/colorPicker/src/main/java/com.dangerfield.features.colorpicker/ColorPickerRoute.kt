package com.dangerfield.features.colorpicker

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val colorPickerRoute = route("color_picker")

fun Router.navigateToColorPicker() {
    navigate(colorPickerRoute.noArgRoute())
}
