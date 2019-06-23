package com.dangerfield.spyfall.settings


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import kotlinx.android.synthetic.main.alert_custom.view.*
import kotlinx.android.synthetic.main.alert_custom.view.btn_custom_alert_negative
import kotlinx.android.synthetic.main.alert_custom.view.btn_custom_alert_positive
import kotlinx.android.synthetic.main.alert_custom.view.tv_custom_alert_message
import kotlinx.android.synthetic.main.alert_custom.view.tv_custom_alert_title
import kotlinx.android.synthetic.main.fragment_settings.*
import android.content.Context.MODE_PRIVATE





class SettingsFragment : Fragment() {

    lateinit var colors: MutableList<ColorButton>
    lateinit var colorChangeAdapter: ColorChangeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        colors = mutableListOf()
        colors.add(ColorButton(Color.parseColor("#C388B3"),false))
        colors.add(ColorButton(Color.parseColor("#D65656"),false))
        colors.add(ColorButton(Color.parseColor("#F56E16"),false))
        colors.add(ColorButton(Color.parseColor("#39A80C"),false))
        colors.add(ColorButton(Color.parseColor("#009BFF"),false))
        colors.add(ColorButton(Color.parseColor("#634FEC"),false))
        colorChangeAdapter = ColorChangeAdapter(colors, context)

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_theme_change.setOnClickListener{
           showColorChangeDialog()
        }
    }

    fun showColorChangeDialog(){
        val dialogBuilder = AlertDialog.Builder(context!!)
        var view = LayoutInflater.from(context).inflate(R.layout.alert_custom, null)
        dialogBuilder.setView(view)
        var dialog = dialogBuilder.create()
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true)

        view.apply {

            rv_color_change.visibility = View.VISIBLE

            rv_color_change.adapter = colorChangeAdapter
            rv_color_change.layoutManager = GridLayoutManager(context,4)
            rv_color_change.setHasFixedSize(true)

            btn_custom_alert_negative.setOnClickListener { dialog.cancel() }

            btn_custom_alert_positive.setOnClickListener {
                val chosenColor = colorChangeAdapter.colors[colorChangeAdapter.selectedPosition].color
                UIHelper.accentColor = chosenColor
                saveColor(chosenColor)
                dialog.dismiss() }

            btn_custom_alert_negative.text = "Cancel"
            btn_custom_alert_positive.text = "Change"
            //for theme changing
            btn_custom_alert_positive.background.setTint(UIHelper.accentColor)
            tv_custom_alert_message.text = "Choose your new theme"
            tv_custom_alert_title.text = "Change Theme"

        }

        dialog.show()

    }

    fun saveColor(chosenColor: Int){
        val editor = context!!.getSharedPreferences(resources.getString(R.string.shared_preferences), MODE_PRIVATE).edit()
        editor.putInt(resources.getString(R.string.shared_preferences_color), chosenColor)
        editor.apply()
    }


}
