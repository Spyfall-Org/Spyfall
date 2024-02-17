package com.dangerfield.libraries.config

import se.ansman.dagger.auto.BindGenericAs

/**
 * Represents an experiment being conducted.
 *
 * Experiments differ from [ConfiguredValue]s in that they are not simple key value mappings but
 * rather include a choice between a control and test experience.
 *
 * In order for an [Experiment] to be view in the QA screen,
 * it must be bound into a set using the [@AutoBindIntoSet]
 */
@BindGenericAs.Default(BindGenericAs.Wildcard)
abstract class Experiment<out T : Any> {
    /**
     * The name displayed for the experiment in the QA screen.
     */
    abstract val displayName: String

    /**
     * The description displayed for the experiment in the QA screen.
     */
    open val description: String? = null

    /**
     * The unique identifier for the experiment. Should be human readable.
     * ex:
     * ```kotlin
     * MyExperiment : Experiment<Int>() {
     *  override val id: String = "my_experiment"
     *  ...
     * }
     * ```
     */
    abstract val id: String

    /**
     * The value for the experiment when the experiment is OFF
     */
    abstract val control: T

    /**
     * The value for the experiment when the experiment is ON
     */
    abstract val test: T

    /**
     * The value for the experiment when no value is found
     */
    open val default: T
        get() = control

    /**
     * Flag used to ensure experiment is only active in debug builds
     * This allows for releases of builds with experiments that are still in development
     */
    abstract val isDebugOnly: Boolean

    val path : String
        get() = "experiments.$id"

    open val showInQaExperiments: Boolean = true

    val value: T
        get() = resolveValue()

    abstract fun resolveValue(): T

    operator fun invoke(): T = this.value
}