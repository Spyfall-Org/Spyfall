package spyfallx.coreui

import androidx.fragment.app.Fragment

fun Fragment.requireSupportFragmentManager() = requireActivity().supportFragmentManager

val Fragment.supportFragmentManager
    get() = activity?.supportFragmentManager