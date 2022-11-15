package spyfallx.core

/**
 * Convenience method for only executing logic if the parameters are not null
 */
fun <A, B, T> allOrNone(one: A?, two: B?, block: (A, B) -> T): T? = if (one != null && two != null) block(one, two) else null

/**
 * Convenience method for only executing logic if the parameters are not null
 */
fun <A, B, C, T> allOrNone(one: A?, two: B?, three: C?, block: (A, B, C) -> T): T? =
    if (one != null && two != null && three != null) block(one, two, three) else null

/**
 * Convenience method for only executing logic if the parameters are not null
 */
fun <A, B, C, D, T> allOrNone(one: A?, two: B?, three: C?, four: D?, block: (A, B, C, D) -> T): T? =
    if (one != null && two != null && three != null && four != null) block(one, two, three, four) else null

/**
 * Convenience Method to make it more readable when do logic needs to be executed
 */
fun Any.doNothing() = Unit
