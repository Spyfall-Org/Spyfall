package com.dangerfield.features.forcedupdate


interface IsAppUpdateRequired {
    suspend operator fun invoke(): Boolean
}
