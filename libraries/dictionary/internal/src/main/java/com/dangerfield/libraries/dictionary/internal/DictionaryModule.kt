package com.dangerfield.libraries.dictionary.internal

import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.DictionaryFlow
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DictionaryModule {

    @Provides
    fun providesDictionary(repo: DictionaryRepository): Dictionary {
        return repo.dictionary()
    }

    @Provides
    fun providesDictionaryFlow(repo: DictionaryRepository): DictionaryFlow {
        return DictionaryFlow(repo.dictionaryStream())
    }
}
