package com.dangerfield.libraries.test

import com.dangerfield.libraries.config.ConfiguredValue

class FakeConfiguredValue<T : Any>(
    value: T,
) : ConfiguredValue<T>() {

    override val displayName: String = "FakeConfiguredValue"
    override val default: T = value
    override fun resolveValue(): T = value
}