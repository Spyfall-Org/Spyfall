package com.dangerfield.spyfall.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.alert_custom.view.*
import kotlinx.android.synthetic.main.dialog_packs.view.*
import android.graphics.PorterDuff
import android.util.TypedValue
import androidx.core.content.ContextCompat
import android.widget.TextView
import androidx.annotation.ColorInt
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import com.dangerfield.spyfall.R


class UIHelper {

    companion object {

        var accentColor: Int = Color.parseColor("#E3212F")

        var accentColors =
            mutableListOf(
                Color.parseColor("#9533C7"),
                Color.parseColor("#00A0EF"),
                Color.parseColor("#2FD566"),
                Color.parseColor("#FF5800"),
                Color.parseColor("#E3212F"))

        val keyboardHider = View.OnFocusChangeListener { view, b ->
            if (!b) { this.hideKeyboardFrom(view) }
        }

        private fun hideKeyboardFrom(view: View) {
            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun getSavedColor(context: Context){
            val prefs = context.getSharedPreferences(context.resources.getString(R.string.shared_preferences), Context.MODE_PRIVATE)
            val savedColor: Int = prefs.getInt(context.resources.getString(R.string.shared_preferences_color), 0)
            if (savedColor != 0) {
                accentColor = if(savedColor == Color.WHITE) accentColors.random() else savedColor
            }
        }

        fun errorDialog(context: Context) = UIHelper.customSimpleAlert(context,context.resources.getString(R.string.error_title),
            context.resources.getString(R.string.error_message),context.resources.getString(R.string.positive_action_standard),{},"",{})

        fun packsDialog(context: Context, packsList: MutableList<List<String>>): AlertDialog {

            val dialogBuilder = AlertDialog.Builder(context)
            Log.d("Custom alerts", "Setting packs view")
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_packs, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            //TODO: make dynamic by pulling from firebase and adding recycler views as needed
            //function would need to accept an array of lists, and cycle through them
            //TOD DO THIS WE MIGHT HAVE TO MAKE THE PARENT VIEW A LINEAR LAYOUT

            view.apply{
                tv_dialog_pack1_header.text = packsList[0][0]
                rv_dialog_pack1.adapter = SimpleTextAdapter(packsList[0].subList(1,packsList[0].size-1), context)
                rv_dialog_pack1.layoutManager = GridLayoutManager(context,2)
                rv_dialog_pack1.setHasFixedSize(true)

                tv_dialog_pack2_header.text = packsList[1][0]
                rv_dialog_pack2.adapter = SimpleTextAdapter(packsList[1].subList(1,packsList[1].size-1), context)
                rv_dialog_pack2.layoutManager = GridLayoutManager(context,2)
                rv_dialog_pack2.setHasFixedSize(true)

                tv_dialog_pack3_header.text = packsList[2][0]
                rv_dialog_pack3.adapter = SimpleTextAdapter(packsList[2].subList(1,packsList[2].size-1), context)
                rv_dialog_pack3.layoutManager = GridLayoutManager(context,2)
                rv_dialog_pack3.setHasFixedSize(true)

                btn_dialog_packs_positive.background.setTint(accentColor)
            }

            view.btn_dialog_packs_positive.setOnClickListener{dialog.dismiss()}

            return dialog
        }

        fun customSimpleAlert(
            context: Context, title: String, message: String, positiveText: String, positiveAction: (() -> Unit),
            negativeText: String, negativeAction: (() -> Unit)
        ): Dialog {

            val dialogBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.alert_custom, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            view.apply {
                if (negativeText.isEmpty()) {
                    //remove the negative button
                    btn_custom_alert_negative.visibility = View.GONE

                    val set = ConstraintSet()
                    val layout = custom_alert_view as ConstraintLayout
                    set.clone(layout)
                    // remove all connections.
                    set.clear(R.id.btn_custom_alert_positive, ConstraintSet.END)
                    set.clear(R.id.btn_custom_alert_positive, ConstraintSet.START)
                    //center it
                    set.connect(
                        R.id.btn_custom_alert_positive,
                        ConstraintSet.END,
                        R.id.custom_alert_view,
                        ConstraintSet.END
                    )
                    set.connect(
                        R.id.btn_custom_alert_positive,
                        ConstraintSet.START,
                        R.id.custom_alert_view,
                        ConstraintSet.START
                    )
                    set.applyTo(layout)
                }

                if(title.trim() == context.resources.getString(R.string.about_title)) {
                    btn_email.visibility = View.VISIBLE
                }

                btn_custom_alert_negative.setOnClickListener { negativeAction.invoke(); dialog.cancel() }
                btn_custom_alert_positive.setOnClickListener { positiveAction.invoke(); dialog.dismiss() }
                btn_custom_alert_negative.text = negativeText
                btn_custom_alert_positive.text = positiveText
                //for theme changing
                btn_custom_alert_positive.background.setTint(accentColor)
                tv_custom_alert_message.text = message
                tv_custom_alert_title.text = title
            }
            return dialog
        }

        fun setCursorColor(view: EditText, @ColorInt color: Int) {
            try {
                // Get the cursor resource id
                var field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                val drawableResId = field.getInt(view)

                // Get the editor
                field = TextView::class.java.getDeclaredField("mEditor")
                field.isAccessible = true
                val editor = field.get(view)

                // Get the drawable and set a color filter
                val drawable = ContextCompat.getDrawable(view.context, drawableResId)
                drawable!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                val drawables = arrayOf(drawable, drawable)

                // Set the drawables
                field = editor.javaClass.getDeclaredField("mCursorDrawable")
                field.isAccessible = true
                field.set(editor, drawables)
            } catch (ignored: Exception) {
            }
        }
    }
}

fun Float.dp(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
    this, context.resources.displayMetrics).toInt()