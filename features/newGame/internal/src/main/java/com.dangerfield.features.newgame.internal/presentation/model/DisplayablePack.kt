package com.dangerfield.features.newgame.internal.presentation.model

import androidx.core.text.isDigitsOnly
import com.dangerfield.libraries.game.LocationPack

data class DisplayablePack(
    val locationPack: LocationPack,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true,
    val number: String? = locationPack.name.split(" ").firstOrNull() { it.isDigitsOnly() },
    val type: String = locationPack.name.split(" ").filter { !it.isDigitsOnly() }.joinToString(" "),
)

