package spyfallx.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset
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

@OptIn(ExperimentalContracts::class)
inline fun Modifier.then(factory: Modifier.() -> Modifier): Modifier {
    contract {
        callsInPlace(factory, InvocationKind.AT_MOST_ONCE)
    }
    return then(factory(Modifier))
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


/**
 * Draws circle with a solid [color] behind the content.
 *
 * @param color The color of the circle.
 * @param padding The padding to be applied externally to the circular shape. It determines the spacing between
 * the edge of the circle and the content inside.
 *
 * @return Combined [Modifier] that first draws the background circle and then centers the layout.
 */
fun Modifier.circleBackground(color: Color, padding: Dp): Modifier {
    val backgroundModifier = drawBehind {
        drawCircle(color, size.width / 2f, center = Offset(size.width / 2f, size.height / 2f))
    }

    val layoutModifier = layout { measurable, constraints ->
        // Adjust the constraints by the padding amount
        val adjustedConstraints = constraints.offset(-padding.roundToPx())

        // Measure the composable with the adjusted constraints
        val placeable = measurable.measure(adjustedConstraints)

        // Get the current max dimension to assign width=height
        val currentHeight = placeable.height
        val currentWidth = placeable.width
        val newDiameter = maxOf(currentHeight, currentWidth) + padding.roundToPx() * 2

        // Assign the dimension and the center position
        layout(newDiameter, newDiameter) {
            // Place the composable at the calculated position
            placeable.placeRelative((newDiameter - currentWidth) / 2, (newDiameter - currentHeight) / 2)
        }
    }

    return this then backgroundModifier then layoutModifier
}

