package com.dangerfield.features.newgame.internal.presentation.model

import androidx.core.text.isDigitsOnly
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem

data class NewGamePackOption(
    val packName: String,
    val pack: Pack<PackItem>?,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true,
    val number: String? = packName.split(" ").firstOrNull() { it.isDigitsOnly() },
    val type: String = packName.split(" ").filter { !it.isDigitsOnly() }.joinToString(" "),
) {
    constructor(
        pack: Pack<PackItem>,
        isSelected: Boolean = false,
        isEnabled: Boolean = true
    ) : this(
        packName = pack.name,
        pack = pack,
        isSelected = isSelected,
        isEnabled = isEnabled
    )

    constructor(
        name: String,
        isSelected: Boolean = false,
        isEnabled: Boolean = true
    ) : this(
        packName = name,
        pack = null,
        isSelected = isSelected,
        isEnabled = isEnabled
    )
}
