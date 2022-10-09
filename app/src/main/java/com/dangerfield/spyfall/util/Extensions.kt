package com.dangerfield.spyfall.util

import android.app.Activity
import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.game.LegacyGameFragment
import com.dangerfield.spyfall.ui.waiting.LegacyWaitingFragment
import kotlinx.coroutines.*


fun EditText.addCharacterMax(max: Int){
    filters = arrayOf(InputFilter.LengthFilter(max))
}

fun View.goneIf(predicate: Boolean) {
    visibility = if(predicate) View.GONE else View.VISIBLE
}

fun View.visibleIf(predicate: Boolean) {
    visibility = if(predicate) View.VISIBLE else View.INVISIBLE
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

fun View.invisibleIf(condition: Boolean) {
    visibility = if(condition) View.INVISIBLE else View.VISIBLE
}


fun TextView.containsValidOrEmptyEmail(): Boolean {
    val target = this.text
    return Patterns.EMAIL_ADDRESS.matcher(target).matches() || target.toString().trim().isEmpty()
}

suspend fun <A, B> Iterable<A>.pmap(dispatcher: CoroutineDispatcher = Dispatchers.Default, f: suspend (A) -> B): List<B> =
    map { CoroutineScope(dispatcher).async(dispatcher) {  f(it) } }.awaitAll()


fun LegacyWaitingFragment.getViewModelFactory(bundle: Bundle): WaitingViewModelFactory {
    //banging because navigation to waiting should be impossible without the arg
    val currentSession: Session = bundle.getParcelable(LegacyWaitingFragment.SESSION_KEY)!!
    return WaitingViewModelFactory(currentSession)
}

fun LegacyGameFragment.getViewModelFactory(bundle: Bundle): GameViewModelFactory {
    //banging because navigation to game should be impossible without the arg
    val currentSession: Session = bundle.getParcelable(LegacyWaitingFragment.SESSION_KEY)!!
    return GameViewModelFactory(currentSession)
}



