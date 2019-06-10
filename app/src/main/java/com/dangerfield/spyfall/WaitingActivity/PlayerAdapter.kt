package com.dangerfield.spyfall.WaitingActivity

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.change_name.*
import kotlinx.android.synthetic.main.item_player_card.view.*

class PlayerAdapter(var playerName: String?, val playerList: ArrayList<String>, val context: Context) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_player_card,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number?.text = (position + 1).toString()
        holder.name?.text = playerList[position]
        if(holder.name.text == playerName) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.change_name)
            holder.pencil.visibility = View.VISIBLE
            holder.pencil.setOnClickListener {view ->
                dialog.apply{
                    btn_okay.setOnClickListener{
                        var newName = tv_name_change.text.toString()
                        var oldName = holder.name.text.toString()
                        if(!newName.isEmpty()){
                            WaitingGame.changeName(newName,oldName,playerList)
                            dialog.dismiss()
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






