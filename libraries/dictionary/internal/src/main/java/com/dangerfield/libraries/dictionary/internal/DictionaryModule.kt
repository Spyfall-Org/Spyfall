package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import com.dangerfield.libraries.dictionary.Dictionary
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import oddoneout.core.BuildInfo

@Module
@InstallIn(SingletonComponent::class)
object DictionaryModule {

    @Provides
    fun providesDictionary(
        defaultDictionary: ResourceXmlDictionary,
        overrideDictionary: OverrideDictionary,
        buildInfo: BuildInfo,
        @ApplicationContext context: Context
    ): Dictionary {
        return AppDictionary(
            defaultDictionary = defaultDictionary,
            overrideDictionary = overrideDictionary,
            buildInfo = buildInfo,
            context = context
        )
    }
}
