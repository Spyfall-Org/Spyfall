package com.dangerfield.libraries.ui

import androidx.compose.runtime.Composable

/**
 * Convenience method for only compseing logic if the parameters are not null
 */
@Composable
fun <A, B, T> allOrNone(one: A?, two: B?, block: (A, B) -> T): T? = if (one != null && two != null) block(one, two) else null
