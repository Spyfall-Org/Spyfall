package com.dangerfield.libraries.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember

/**
 * A version of [remember] that also passes the key to the creator lambda.
 *
 * This allows you to use function references in some cases:
 * ```
 * val thing = rememberWithKey(key1, ::SomeThing)
 * ```
 *
 * @see remember
 */
@Composable
inline fun <K1, T> rememberWithKey(
    key1: K1,
    crossinline creator: @DisallowComposableCalls (K1) -> T,
): T = remember(key1) { creator(key1) }

/**
 * A version of [remember] that also passes the keys to the creator lambda.
 *
 * This allows you to use function references in some cases:
 * ```
 * val thing = rememberWithKey(key1, key2, ::SomeThing)
 * ```
 *
 * @see remember
 */
@Composable
inline fun <K1, K2, T> rememberWithKey(
    key1: K1,
    key2: K2,
    crossinline creator: @DisallowComposableCalls (K1, K2) -> T,
): T = remember(key1, key2) { creator(key1, key2) }

/**
 * A version of [remember] that also passes the keys to the creator lambda.
 *
 * This allows you to use function references in some cases:
 * ```
 * val thing = rememberWithKey(key1, key2, key3, ::SomeThing)
 * ```
 *
 * @see remember
 */
@Composable
inline fun <K1, K2, K3, T> rememberWithKey(
    key1: K1,
    key2: K2,
    key3: K3,
    crossinline creator: @DisallowComposableCalls (K1, K2, K3) -> T,
): T = remember(key1, key2, key3) { creator(key1, key2, key3) }

/**
 * A version of [remember] that also passes the keys to the creator lambda.
 *
 * This allows you to use function references in some cases:
 * ```
 * val thing = rememberWithKey(key1, key2, key3, key4, ::SomeThing)
 * ```
 *
 * @see remember
 */
@Composable
inline fun <K1, K2, K3, K4, T> rememberWithKey(
    key1: K1,
    key2: K2,
    key3: K3,
    key4: K4,
    crossinline creator: @DisallowComposableCalls (K1, K2, K3, K4) -> T,
): T = remember(key1, key2, key3, key4) { creator(key1, key2, key3, key4) }
