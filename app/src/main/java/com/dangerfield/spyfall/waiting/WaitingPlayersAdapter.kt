package com.dangerfield.spyfall.waiting

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.game.GameViewModel
import kotlinx.android.synthetic.main.change_name.*
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
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.change_name)
            holder.pencil.visibility = View.VISIBLE

            holder.pencil.setOnClickListener {view ->
                dialog.apply{
                    btn_okay.setOnClickListener{
                        var newName = tv_name_change.text.toString().trim()
                        //as long as they typed some name that doesnt already exist
                        if(newName.isNotEmpty() && !viewModel.gameObject.value!!.playerList.contains(newName)){

                            viewModel.changeName(newName)?.addOnCompleteListener {
                                //once the name has changed, dismiss the dialog
                                dialog.dismiss()
                            }.addOnCanceledListener {

                            }
                        }else{
                            Toast.makeText(context, "enter a name that is not in the game", Toast.LENGTH_LONG).show()
                        }
                    }
                    btn_cancel.setOnClickListener{
                        dialog.dismiss()
                    }
                }.show()

            }
        }


    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val number = view.tv_player_number
        val name = view.tv_new_game_name
        val pencil = view.iv_pencil

    }



}
