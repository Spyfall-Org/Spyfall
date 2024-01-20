package com.dangerfield.libraries.dictionary.internal

import kotlinx.coroutines.flow.Flow
import oddoneout.core.Try

/**
 * Abstraction to encapsulate the logic behind the local storage of the dictionary.
 *
 */
interface CachedDictionaryDataSource {
    /**
     * @return a flow that emits a value for every update to the local Dictionary
     */
    fun getDictionaryFlow(): Flow<OverrideDictionary>

    /**
     * Gets the last cached dictionary
     */
    suspend fun getDictionary(): Try<OverrideDictionary>

    /**
     * updates the locally stored Dictionary
     */
    suspend fun updateDictionary(dictionary: OverrideDictionary)
}
