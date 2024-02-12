package com.dangerfield.features.newgame.internal.presentation.model

import androidx.core.text.isDigitsOnly
import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.Pack

data class DisplayablePack(
    val pack: Pack,
    val isSelected: Boolean = false,
    val number: String = pack.name.split(" ").first { it.isDigitsOnly() },
    val type: String = pack.name.split(" ").filter { !it.isDigitsOnly() }.joinToString(" "),
)

