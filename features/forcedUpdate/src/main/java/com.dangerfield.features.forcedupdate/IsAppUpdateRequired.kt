package com.dangerfield.features.forcedupdate

fun interface IsAppUpdateRequired {
    suspend operator fun invoke(): Boolean
}