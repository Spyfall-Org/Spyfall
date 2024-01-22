package com.dangerfield.features.termOfService

import kotlinx.coroutines.flow.Flow

interface GetLegalAcceptanceState {
    suspend operator fun invoke(): Flow<LegalAcceptanceState>
}

enum class LegalAcceptanceState {
    Accepted, NotAccepted
}