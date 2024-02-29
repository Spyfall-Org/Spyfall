package com.dangerfield.features.forcedupdate

import kotlinx.coroutines.flow.Flow


interface IsAppUpdateRequired {
    suspend operator fun invoke(): Flow<Boolean>
}
