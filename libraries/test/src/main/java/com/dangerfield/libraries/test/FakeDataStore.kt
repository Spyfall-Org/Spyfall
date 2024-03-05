package com.dangerfield.libraries.test

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class FakeDataStore<T>(private val initial: T) : DataStore<T> {
    override val data: MutableStateFlow<T> = MutableStateFlow(initial)

    fun clearData() {
        data.value = initial
    }

    fun hasData(predicate: (T) -> Boolean): Boolean = predicate(data.value)

    fun setData(t: T) {
        data.value = t
    }

    override suspend fun updateData(transform: suspend (t: T) -> T): T = data.updateAndGet { transform(it) }
}

fun <T> FakeDataStore<List<T>>.containsItem(predicate: (T) -> Boolean): Boolean = hasData {
    it.any(predicate)
}

fun <T> FakeDataStore<List<T>>.doesNotContainsItem(predicate: (T) -> Boolean): Boolean = hasData {
    it.none(predicate)
}

class ResetDataStoreRule<T>(private val fakeDataStore: FakeDataStore<T>) : TestRule {
    override fun apply(base: Statement, description: Description?): Statement = object : Statement() {
        override fun evaluate() {
            fakeDataStore.clearData()
            base.evaluate()
        }
    }
}