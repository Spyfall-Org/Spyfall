package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.SessionFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SessionFlowImpl @Inject constructor(
    private val repository: SessionRepository
): SessionFlow {

    override suspend fun collect(collector: FlowCollector<Session>) {
        repository.sessionFlow.collect(collector)
    }
}