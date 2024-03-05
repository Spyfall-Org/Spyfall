package com.dangerfield.libraries.test

import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.CoroutineContext

private const val DefaultDispatchTimeoutMs = 5_000L

/**
 * Using this class allows for:
 * 1. Multiple dispatchers that share the same scheduler (needed for predictable results)
 * 2. Setting and resetting main
 * 3. Setting a timeout on tests (helpful to our CI)
 */
class CoroutinesTestRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
    private val dispatchTimeoutMs: Long = DefaultDispatchTimeoutMs,
) : TestWatcher(), CoroutineContext by dispatcher {

    val scheduler = dispatcher.scheduler

    val testScope: TestScope = TestScope(dispatcher)

    val dispatcherProvider = object : DispatcherProvider {
        override val default = dispatcher
        override val unconfined = dispatcher
        override val main = dispatcher
        override val io = dispatcher
    }

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    fun test(block: suspend TestScope.() -> Unit): Unit = runTest(dispatcher, dispatchTimeoutMs, block)
}
