package com.dangerfield.spyfall

import androidx.annotation.VisibleForTesting
import org.mockito.Mockito

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> anyObject(): T {
    return Mockito.anyObject<T>()
}