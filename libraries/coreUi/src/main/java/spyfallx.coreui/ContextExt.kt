package spyfallx.coreui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context?.showToast(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    showToast(this?.getString(stringRes) ?: return, duration)
}
fun Context?.showToast(str: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this ?: return, str, duration).show()
}
