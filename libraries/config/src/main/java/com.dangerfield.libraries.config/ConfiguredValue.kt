package com.dangerfield.libraries.config

import se.ansman.dagger.auto.BindGenericAs
import java.util.Locale

/**
 * Represents a value in the app config. These are typically stable, key-value pairs used to set up
 * and customize the behavior of the application.
 *
 * Examples include API endpoints, and UI settings.
 *
 * In order for a [ConfiguredValue] to be view in the QA screen,
 * it must be bound into a set using the [@AutoBindIntoSet]
 */
@BindGenericAs.Default(BindGenericAs.Wildcard)
abstract class ConfiguredValue<out T : Any> {
    abstract val displayName: String
    open val description: String? = null

    open val path: String
        get() = javaClass.simpleName.replaceFirstChar { it.lowercase(Locale.getDefault()) }

    abstract val default: T
    open val showInQADashboard: Boolean = false

    open val debugOverride: T? = null

    val value: T
        get() = resolveValue()

    abstract fun resolveValue(): T

    operator fun invoke(): T = this.value
}