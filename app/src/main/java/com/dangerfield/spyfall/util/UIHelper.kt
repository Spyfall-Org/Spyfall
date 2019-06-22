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
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.alert_custom.view.*
import android.graphics.PorterDuff
import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import android.widget.TextView
import androidx.annotation.ColorInt
import android.widget.EditText
import android.widget.ListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.newGame.SimpleAdapter
import org.w3c.dom.Text
import java.util.ArrayList


class UIHelper {

    companion object {

        var accentColor: Int = randomAccentColor()


        val keyboardHider = View.OnFocusChangeListener { view, b ->
            if (!b) {
                this.hideKeyboardFrom(view)
            }
        }

        fun randomAccentColor(): Int {

            var colors = mutableListOf<Int>()
            colors.add(Color.parseColor("#C388B3"))
            colors.add(Color.parseColor("#D65656"))
            colors.add(Color.parseColor("#F56E16"))
            colors.add(Color.parseColor("#39A80C"))
            colors.add(Color.parseColor("#009BFF"))
            colors.add(Color.parseColor("#BB5DBD"))
            colors.add(Color.parseColor("#634FEC"))

            return colors.random()

        }

        private fun hideKeyboardFrom(view: View) {
            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }


        fun customAlert(
            context: Context, title: String, message: String, positiveText: String, positiveAction: (() -> Unit),
            negativeText: String, negativeAction: (() -> Unit)
        ): Dialog {

            val dialogBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.alert_custom, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            view.apply {
                if (negativeText.isEmpty()) {
                    //remove the negative button
                    Log.d("Alert", "Negative Text is Empty")
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


                if(title == "Packs"){
                    Log.d("Custom alerts","Setting packs view")
                    val constraints = ConstraintSet()

                    val tv_pack_1 = BoldText(context,"Standard pack 1:")
                    val list = listOf("Construction Site","Construction Site","Construction Site","Construction Site"
                    ,"Construction Site","Construction Site","Construction Site","Construction Site")

                    val recyclerView = RecyclerView(context)
                    recyclerView.id = View.generateViewId()
                    recyclerView.layoutManager = GridLayoutManager(context,2)
                    recyclerView.adapter = SimpleAdapter(list as ArrayList<String>, context)


                    custom_alert_view.addView(tv_pack_1)
                    custom_alert_view.addView(recyclerView)


                    constraints.clone(custom_alert_view)


                    constraints.connect(tv_pack_1.id,ConstraintSet.START,R.id.tv_custom_alert_message,ConstraintSet.START)
                    constraints.connect(tv_pack_1.id,ConstraintSet.TOP,R.id.tv_custom_alert_message,ConstraintSet.BOTTOM,24f.dp(context))

                    constraints.clear(R.id.btn_custom_alert_positive, ConstraintSet.TOP)

                    constraints.connect(recyclerView.id,ConstraintSet.START,R.id.tv_custom_alert_message,ConstraintSet.START)
                    constraints.connect(recyclerView.id,ConstraintSet.END,R.id.tv_custom_alert_message,ConstraintSet.END)
                    constraints.connect(recyclerView.id,ConstraintSet.TOP,tv_pack_1.id,ConstraintSet.BOTTOM)

                    constraints.constrainDefaultWidth(recyclerView.id, ConstraintSet.MATCH_CONSTRAINT)
                    constraints.constrainDefaultHeight(recyclerView.id,  ConstraintSet.WRAP_CONTENT)


                    constraints.connect(R.id.btn_custom_alert_positive,ConstraintSet.TOP,recyclerView.id,ConstraintSet.BOTTOM)


                    constraints.applyTo(custom_alert_view)

                }

                btn_custom_alert_negative.setOnClickListener { negativeAction.invoke(); dialog.cancel() }
                btn_custom_alert_positive.setOnClickListener { positiveAction.invoke(); dialog.dismiss() }
                btn_custom_alert_negative.text = negativeText
                btn_custom_alert_positive.text = positiveText
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


        fun Float.dp(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this, context.resources.displayMetrics).toInt()



    }

}