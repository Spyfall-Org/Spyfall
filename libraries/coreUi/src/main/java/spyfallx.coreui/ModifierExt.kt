package spyfallx.coreui

import androidx.compose.ui.Modifier
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns a [Modifier] that combines the modifier returned from the [factory] to the receiver if [predicate] is `true`.
 *
 * This is useful if you want to conditionally apply a modifier, for example:
 * ```
 * Box(
 *   modifier = Modifier
 *      .thenIf(DEBUG) { background(Color.Red) }
 * )
 * ```
 */
@OptIn(ExperimentalContracts::class)
inline fun Modifier.thenIf(predicate: Boolean, factory: Modifier.() -> Modifier): Modifier {
    contract {
        callsInPlace(factory, InvocationKind.AT_MOST_ONCE)
    }
    return if (predicate) then(factory(Modifier)) else this
}

/**
 * Returns a [Modifier] that combines the modifier returned from the [factory] to the receiver if the given [value] is
 * not null.
 *
 * The non null value will be passed to the [factory] as a parameter.
 *
 * This is useful if you want to conditionally apply a modifier, for example:
 * ```
 * Box(
 *   modifier = Modifier
 *      .thenIfNotNull(borderStroke) { border(it) }
 * )
 * ```
 */
@OptIn(ExperimentalContracts::class)
inline fun <T : Any> Modifier.thenIfNotNull(value: T?, factory: Modifier.(T) -> Modifier): Modifier {
    contract {
        callsInPlace(factory, InvocationKind.AT_MOST_ONCE)
    }
    return if (value != null) then(factory(Modifier, value)) else this
}

