package com.dangerfield.libraries.dictionary.internal

import oddoneout.core.Try

interface OverrideDictionaryDataSource {
    suspend fun getDictionary(): Try<OverrideDictionary>
}