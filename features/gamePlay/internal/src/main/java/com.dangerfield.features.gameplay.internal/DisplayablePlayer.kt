package com.dangerfield.features.gameplay.internal

data class DisplayablePlayer(
    val name: String,
    val id: String,
    val role: String,
    val isFirst: Boolean,
    val isOddOneOut: Boolean,
)