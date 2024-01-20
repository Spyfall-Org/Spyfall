package com.dangerfield.libraries.dictionary

import kotlinx.coroutines.flow.Flow

/**
 * This is a little hack so that we can directly inject a DictionaryFlow.
 * Dagger wont let us provide a Flow<Dictionary> but we can provide this custom class.
 */
class DictionaryFlow (
    private val dictionaryFlow: Flow<Dictionary>
): Flow<Dictionary> by dictionaryFlow