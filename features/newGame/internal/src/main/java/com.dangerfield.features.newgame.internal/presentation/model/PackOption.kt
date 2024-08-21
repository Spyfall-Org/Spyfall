package com.dangerfield.features.newgame.internal.presentation.model

import androidx.core.text.isDigitsOnly
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem

sealed class PackOption(
    val new: Boolean,
    val packName: String? = null,
    val packData: com.dangerfield.libraries.game.Pack<PackItem>? = null,
    val isSelected: Boolean = false,
) {


    data class Pack(
        val name: String,
        val pack: com.dangerfield.libraries.game.Pack<PackItem>,
        val selected: Boolean = false,
        val isEnabled: Boolean = true,
        val isNew: Boolean = false,
        val number: String? = name.split(" ").firstOrNull() { it.isDigitsOnly() },
        val type: String = name.split(" ").filter { !it.isDigitsOnly() }.joinToString(" "),
    ) : PackOption(
        new = isNew,
        packName = name,
        packData = pack,
        isSelected = selected
    ) {
        constructor(
            pack: com.dangerfield.libraries.game.Pack<PackItem>,
            isNew: Boolean = false
        ) : this(
            name = pack.packName,
            pack = pack,
            isNew = isNew,
        )
    }

    data class CreatePack(
        val isNew: Boolean = false,
    ) : PackOption(isNew)

    data class EditYourPacks(
        val isNew: Boolean = false,
    ) : PackOption(isNew)
}
