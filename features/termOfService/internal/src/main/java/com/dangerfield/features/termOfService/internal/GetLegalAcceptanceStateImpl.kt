package com.dangerfield.features.termOfService.internal

import com.dangerfield.features.termOfService.GetLegalAcceptanceState
import com.dangerfield.features.termOfService.LegalAcceptanceState
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class GetLegalAcceptanceStateImpl@Inject constructor(
    private val legalAcceptanceRepository: LegalAcceptanceRepository
): GetLegalAcceptanceState {

    override suspend fun invoke(): Flow<LegalAcceptanceState> = legalAcceptanceRepository.getAcceptanceStateFlow()

}