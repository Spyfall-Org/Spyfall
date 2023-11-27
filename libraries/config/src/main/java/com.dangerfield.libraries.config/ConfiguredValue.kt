package com.dangerfield.libraries.config

import se.ansman.dagger.auto.BindGenericAs

@BindGenericAs.Default(BindGenericAs.Wildcard)
abstract class ConfiguredValue<out T : Any> {
    abstract val displayName: String
    open val description: String? = null

    abstract val path: String
    abstract val default: T
    open val showInQADashboard: Boolean = false

    val value: T
        get() = resolveValue()

    abstract fun resolveValue(): T

    operator fun invoke(): T = this.value
}