package com.dangerfield.features.gameplay.internal

import com.dangerfield.libraries.game.Player

data class DisplayablePlayer(
    val name: String,
    val id: String,
    val role: String,
    val isFirst: Boolean,
    val isOddOneOut: Boolean,
)

fun Player.toDisplayable(isFirst: Boolean): DisplayablePlayer =
    DisplayablePlayer(
        name = userName,
        id = id,
        role = role ?: "",
        isFirst = isFirst,
        isOddOneOut = isOddOneOut
    )
