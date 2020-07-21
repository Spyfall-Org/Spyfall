package com.dangerfield.spyfall.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.models.Feedback
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_feedback.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FeedbackHelper(
    private val db: FirebaseFirestore,
    private val constants: Constants
) {

    private var dialogView: View? = null

    fun showFeedbackDialog(context: Context) {
        val dialog = getNewFeedbackDialog(context)
        dialog.show()
        dialogView?.tv_feedback?.openKeyboard()
    }

    private fun getNewFeedbackDialog(context: Context): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_feedback, null)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialogView?.apply {
            btn_feedback_submit.background.setTint(UIHelper.accentColor)
            UIHelper.setCursorColor(tv_feedback, UIHelper.accentColor)
            btn_feedback_submit.setOnClickListener {
                val feedbackText = tv_feedback.text.toString().trim()
                if (feedbackText.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "Looks like your feedback was empty. We would appreciate any feedback you have to give :)",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    submitFeedback(feedbackText, context)
                    Toast.makeText(
                        context,
                        "Thank you for your feedback! We rely on it to bring you a good experience",
                        Toast.LENGTH_LONG
                    ).show()
                    this.tv_feedback.hideKeyboard()
                    dialog.dismiss()
                }
            }
            btn_feedback_cancel.setOnClickListener {
                this.tv_feedback.hideKeyboard()
                dialog.dismiss()
            }
        }
        return dialog
    }

    private fun submitFeedback(message: String, context: Context) {

        val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")

        val apiLevel = "${Build.VERSION.SDK_INT}"
        val device = "${Build.DEVICE}"
        val modelAndProduct = "${Build.MODEL} (${Build.PRODUCT})"
        val appVersion = BuildConfig.VERSION_NAME
        val osVersion = getOSString(Build.VERSION.SDK_INT)

        val feedback = Feedback(
            message = message,
            osVersion = osVersion,
            apiLevel = apiLevel,
            device = device,
            modelAndProduct = modelAndProduct,
            appVersion = appVersion,
            date = dateFormat.format(Date())
        )

        db.collection(constants.feedback).document("Android-" + UUID.randomUUID()).set(feedback)
    }

    private fun getOSString(code: Int): String {
        return when (code) {
            1,
            2 -> "BASE"
            3 -> "CUPCAKE"
            4 -> "DONUT"
            5 -> "ECLAIR"
            6 -> "ECLAIR_0_1"
            7 -> "ECLAIR_MR1"
            8 -> "FROYO"
            9 -> "GINGERBREAD"
            10 -> "GINGERBREAD_MR1"
            11 -> "HONEYCOMB"
            12 -> "HONEYCOMB_MR1"
            13 -> "HONEYCOMB_MR2"
            14 -> "ICE_CREAM_SANDWICH"
            15 -> "ICE_CREAM_SANDWICH_MR1"
            16 -> "JELLY_BEAN"
            17 -> "JELLY_BEAN_MR1"
            18 -> "JELLY_BEAN_MR2"
            19 -> "KITKAT"
            20 -> "KITKAT_WATCH"
            21 -> "LOLLIPOP"
            22 -> "LOLLIPOP_MR1"
            23 -> "M"
            24 -> "N"
            25 -> "N_MR1"
            26 -> "O"
            27 -> "O_MR1"
            28 -> "P"
            29 -> "Q"
            30 -> "R"
            else -> "UNKNOWN VERSION CODE: $code"
        }
    }
}