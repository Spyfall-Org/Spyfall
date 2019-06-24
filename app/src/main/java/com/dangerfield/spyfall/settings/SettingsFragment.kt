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
import android.content.Context.MODE_PRIVATE
import android.graphics.PorterDuff
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    lateinit var colors: MutableList<ColorButton>
    lateinit var colorChangeAdapter: ColorChangeAdapter
    lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        colors = mutableListOf()
        UIHelper.accentColors.forEach { colors.add(ColorButton(it,false)) }
        //this is a flag for random colors
        colors.add(ColorButton(Color.WHITE,false))
        colorChangeAdapter = ColorChangeAdapter(colors, context)


        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(parentFragment!!.view!!)

        btn_theme_change.setOnClickListener{
           showColorChangeDialog()
        }

        val drawable = resources.getDrawable(R.drawable.ic_rules).mutate()
        drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        ic_about.setImageDrawable(drawable)

        btn_about.setOnClickListener{
            UIHelper.customSimpleAlert(context!!,"About",resources.getString(R.string.about_message),"Okay",{},"",{}).show()
        }

        btn_ads.setOnClickListener{
            UIHelper.customSimpleAlert(context!!,"Remove ads?",resources.getString(R.string.remove_ads_message),"Upgrade",{},"Cancel",{}).show()
        }
    }


    //TODO: consider making a view model for this
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
                if(colorChangeAdapter.selectedPosition != -1) {
                    val chosenColor = colorChangeAdapter.colors[colorChangeAdapter.selectedPosition].color
                    //color white is the flag for random colors
                    UIHelper.accentColor = if(chosenColor == Color.WHITE) UIHelper.accentColors.random() else chosenColor
                    saveColor(chosenColor)
                    dialog.dismiss()
                }else{
                    Toast.makeText(context!!,"No Color was selected",Toast.LENGTH_LONG).show()
                }
            }
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
