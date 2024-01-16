package com.dangerfield.features.gameplay.internal

@Suppress("ImplicitDefaultLocale")
fun Long.millisToMMss(): String {
    val minutes = this / 60000
    val seconds = (this % 60000) / 1000
    return String.format("%02d:%02d", minutes, seconds)
}