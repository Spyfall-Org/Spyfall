package com.dangerfield.spyfall.legacy.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.dialog_custom.view.*
import kotlinx.android.synthetic.main.dialog_packs.view.*
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.dialog_review.view.*


class UIHelper {

    companion object {

        var accentColor: Int = Color.parseColor("#E3212F")

        var accentColors =
            mutableListOf(
                Color.parseColor("#9533C7"),
                Color.parseColor("#00A0EF"),
                Color.parseColor("#2FD566"),
                Color.parseColor("#FF5800"),
                Color.parseColor("#E3212F")
            )

        fun getSavedColor(context: Context) {
            val prefs = context.getSharedPreferences(
                context.resources.getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )
            val savedColor = prefs.getInt(
                context.resources.getString(R.string.shared_preferences_color),
                Color.WHITE
            )
            accentColor = if (savedColor == Color.WHITE) accentColors.random() else savedColor
        }

        fun errorDialog(context: Context) = customSimpleAlert(context,
            context.resources.getString(R.string.error_title),
            context.resources.getString(R.string.newtork_error_message),
            context.resources.getString(R.string.positive_action_standard),
            {},
            "",
            {})

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

            view.apply {
                tv_dialog_pack1_header.text = packsList[0][0]
                rv_dialog_pack1.adapter =
                    SimpleTextAdapter(packsList[0].subList(1, packsList[0].size), context)
                rv_dialog_pack1.layoutManager = GridLayoutManager(context, 2)
                rv_dialog_pack1.setHasFixedSize(true)

                tv_dialog_pack2_header.text = packsList[1][0]
                rv_dialog_pack2.adapter =
                    SimpleTextAdapter(packsList[1].subList(1, packsList[1].size), context)
                rv_dialog_pack2.layoutManager = GridLayoutManager(context, 2)
                rv_dialog_pack2.setHasFixedSize(true)

                tv_dialog_pack3_header.text = packsList[2][0]
                rv_dialog_pack3.adapter =
                    SimpleTextAdapter(packsList[2].subList(1, packsList[2].size), context)
                rv_dialog_pack3.layoutManager = GridLayoutManager(context, 2)
                rv_dialog_pack3.setHasFixedSize(true)

                btn_dialog_packs_positive.background.setTint(accentColor)
            }

            view.btn_dialog_packs_positive.setOnClickListener { dialog.dismiss() }

            return dialog
        }

        fun customSimpleAlert(
            context: Context,
            title: String,
            message: String,
            positiveText: String,
            positiveAction: (() -> Unit),
            negativeText: String,
            negativeAction: (() -> Unit),
            leftAlignText: Boolean = false
        ): Dialog {

            val dialogBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            view.apply {
                if (leftAlignText) {
                    tv_custom_alert.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
                }
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

                if (title.trim() == context.resources.getString(R.string.about_title)) {
                    btn_email.visibility = View.VISIBLE
                    btn_email.setLinkTextColor(accentColor)
                }

                btn_custom_alert_negative.setOnClickListener { negativeAction.invoke(); dialog.cancel() }
                btn_custom_alert_positive.setOnClickListener { positiveAction.invoke(); dialog.dismiss() }
                btn_custom_alert_negative.text = negativeText
                btn_custom_alert_positive.text = positiveText
                //for theme changing
                btn_custom_alert_positive.background.setTint(accentColor)
                tv_custom_alert.text = message

                tv_custom_alert_title.text = title
            }
            return dialog
        }

        fun getReviewDialog(
            context: Context,
            positiveAction: (() -> Unit),
            negativeAction: (() -> Unit) = {}
        ): AlertDialog {
            val dialogBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_review, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            view?.apply {
                btn_positive.setOnClickListener { positiveAction.invoke(); dialog.cancel() }
                btn_negative.setOnClickListener { negativeAction.invoke(); dialog.dismiss() }
                //for theme changing
                btn_positive.background.setTint(accentColor)
            }
            return dialog
        }

        fun updateDrawableToTheme(context: Context, id: Int) {
            val unwrappedDrawable =
                AppCompatResources.getDrawable(context, id)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, accentColor)
        }
    }
}
