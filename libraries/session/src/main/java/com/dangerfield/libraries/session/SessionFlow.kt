package com.dangerfield.libraries.session

import kotlinx.coroutines.flow.Flow

/**
 * We cannot directly add a Flow<X> to the dependency graph, this is a workaround to provide a
 * way to listen to config changes easily.
 */
interface SessionFlow: Flow<Session>
