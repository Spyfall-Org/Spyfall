package com.dangerfield.spyfall.waiting

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.game.GameViewModel
import kotlinx.android.synthetic.main.alert_change_name.*
import kotlinx.android.synthetic.main.alert_change_name.view.*
import kotlinx.android.synthetic.main.item_player_card.view.*

class WaitingPlayersAdapter(val context: Context, playerList: ArrayList<String>, var viewModel: GameViewModel) : RecyclerView.Adapter<WaitingPlayersAdapter.ViewHolder>() {

    var players = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    init {
        this.players = playerList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_player_card, parent, false))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number?.text = (position + 1).toString()
        holder.name?.text = players[position]

        if(holder.name.text.trim() == viewModel.currentUser.trim()) {
            val dialogBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.alert_change_name, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()

            holder.pencil.visibility = View.VISIBLE

            holder.pencil.setOnClickListener {_ ->
                view.apply{
                    btn_change_name_alert_okay.setOnClickListener{
                        var newName = tv_alert_change_name.text.toString().trim()
                        //as long as they typed some name that doesnt already exist
                        if(newName.isNotEmpty() && newName.length < 25 && !viewModel.gameObject.value!!.playerList.contains(newName)){

                            viewModel.changeName(newName)?.addOnCompleteListener {
                                //once the name has changed, dismiss the dialog
                                dialog.dismiss()
                            }
                        }else{
                            if(newName.length>25){
                                Toast.makeText(context, "please enter a name less than 25 characters", Toast.LENGTH_LONG).show()
                            }
                            else{
                                Toast.makeText(context, "enter a name that is not in the game", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    btn_alert_change_name_canel.setOnClickListener{
                        dialog.dismiss()
                    }
                }
                dialog.show()

            }
        }


    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val number: TextView = view.tv_player_number
        val name: TextView = view.tv_new_game_name
        val pencil: ImageView = view.iv_pencil

    }



}
