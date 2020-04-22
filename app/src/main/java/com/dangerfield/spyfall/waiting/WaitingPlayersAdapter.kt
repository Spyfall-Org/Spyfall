package com.dangerfield.spyfall.waiting

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.game.GameViewModel
import com.dangerfield.spyfall.util.UIHelper
import kotlinx.android.synthetic.main.alert_change_name.view.*
import kotlinx.android.synthetic.main.item_player_card.view.*

class WaitingPlayersAdapter(val context: Context, playerList: ArrayList<String>, var viewModel: GameViewModel) : RecyclerView.Adapter<WaitingPlayersAdapter.ViewHolder>() {

    var players = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init { this.players = playerList }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val number: TextView = view.tv_player_number
        val name: TextView = view.tv_new_game_name
        val pencil: ImageView = view.iv_pencil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_player_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number.text = (position + 1).toString()
        holder.name.text = players[position]

        if(holder.name.text.trim() == viewModel.currentUser.trim()) {
            val dialogBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.alert_change_name, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            holder.pencil.visibility = View.VISIBLE

            holder.pencil.setOnClickListener {
                Crashlytics.log("Username: ${viewModel.currentUser} clicked button to change name")

                view.apply{
                    btn_change_name_alert_okay.background.setTint(UIHelper.accentColor)
                    UIHelper.setCursorColor(tv_alert_change_name,UIHelper.accentColor)
                    btn_change_name_alert_okay.setOnClickListener{
                        var newName = tv_alert_change_name.text.toString().trim()
                        //as long as they typed some name that doesnt already exist
                        if(newName.isNotEmpty() && newName.length < 25 && !viewModel.gameObject.value!!.playerList.contains(newName) &&
                            viewModel.gameObject.value?.playerObjectList?.size ?: 0 == 0){

                            Crashlytics.log(" ${viewModel.currentUser} attempting to change name to $newName")

                            val task = viewModel.changeName(newName)
                            if(task!= null) task.addOnCompleteListener { dialog.dismiss() } else dialog.dismiss()

                        }else{

                            if(viewModel.gameObject.value?.playerObjectList?.size ?: 0 > 0){
                                //the user tried to change their name after somone else clicked start (not allowed)
                                dialog.dismiss()
                                Toast.makeText(context,"Cannot change name after game starts", Toast.LENGTH_SHORT).show()
                            }

                            else if(newName.length>25){
                                Toast.makeText(context, resources.getString(R.string.change_name_character_limit),
                                    Toast.LENGTH_LONG).show()
                            }
                            else{
                                Toast.makeText(context, resources.getString(R.string.change_name_different_name),
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    btn_alert_change_name_canel.setOnClickListener{ dialog.dismiss() }
                }
                dialog.show()
                view.tv_alert_change_name.requestFocus()
                dialog.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
            }
        }
    }

    override fun getItemCount(): Int { return players.size }
}
