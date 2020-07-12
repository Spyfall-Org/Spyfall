package com.dangerfield.spyfall.util

import android.text.InputFilter
import android.widget.EditText
import kotlinx.coroutines.*

fun EditText.addCharacterMax(max: Int){
    filters = arrayOf(InputFilter.LengthFilter(max))
}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

/*
maps each item A to a deferrable B? by accepting a function that takes in A and gives back B?
and calling it asynchronously for all items.
This function returns the first Non null B in the group
 */
suspend fun <A,B> Iterable<A>.findFirstNonNullWhenMapped(f: suspend (A) -> B?): B? = coroutineScope {
    map { async { f(it) } }.awaitAll().find { it != null }
}


