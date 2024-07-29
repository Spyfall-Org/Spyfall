package com.dangerfield.features.newgame.internal.presentation.model

import androidx.core.text.isDigitsOnly
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem

data class DisplayablePack(
    val pack: Pack<PackItem>,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true,
    val number: String? = pack.name.split(" ").firstOrNull() { it.isDigitsOnly() },
    val type: String = pack.name.split(" ").filter { !it.isDigitsOnly() }.joinToString(" "),
)

