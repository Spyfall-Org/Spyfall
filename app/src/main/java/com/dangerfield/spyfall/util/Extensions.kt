package com.dangerfield.spyfall.util

import android.app.Activity
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.dangerfield.spyfall.ui.game.GameFragment
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import kotlinx.coroutines.*

fun EditText.addCharacterMax(max: Int){
    filters = arrayOf(InputFilter.LengthFilter(max))
}

/*
maps each item A to a deferrable B? by accepting a function that takes in A and gives back B?
and calling it asynchronously for all items.
This function returns the first Non null B in the group
 */
suspend fun <A,B> Iterable<A>.findFirstNonNullWhenMapped(f: suspend (A) -> B?): B? = coroutineScope {
    map { async { f(it) } }.awaitAll().find { it != null }
}

fun View.goneIf(predicate: Boolean) {
    visibility = if(predicate) View.GONE else View.VISIBLE
}

fun EditText.openKeyboard() {
    this.requestFocus()
    val imm = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.setHideKeyBoardOnPressAway(): View {
    this.onFocusChangeListener = keyboardHider
    return this
}

private val keyboardHider = View.OnFocusChangeListener { view, b ->
    if (!b) { hideKeyboardFrom(view) }
}

private fun hideKeyboardFrom(view: View) {
    val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun TextView.clear() {
    this.text = ""
}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

fun WaitingFragment.getViewModelFactory(bundle: Bundle): WaitingViewModelFactory {
    //banging because navigation to waiting should be impossible without the arg
    val currentSession: Session = bundle.getParcelable(WaitingFragment.SESSION_KEY)!!
    return WaitingViewModelFactory(currentSession)
}

fun GameFragment.getViewModelFactory(bundle: Bundle): GameViewModelFactory {
    //banging because navigation to game should be impossible without the arg
    val currentSession: Session = bundle.getParcelable(WaitingFragment.SESSION_KEY)!!
    return GameViewModelFactory(currentSession)
}



