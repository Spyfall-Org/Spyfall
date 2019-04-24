package com.dangerfield.spyfall.WaitingActivity

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.MainActivity
import com.dangerfield.spyfall.NewGameActivity
import com.dangerfield.spyfall.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.change_name.*
import kotlinx.android.synthetic.main.change_name.view.*
import kotlinx.android.synthetic.main.player_card.view.*

class PlayerAdapter(var playerName: String, val playerList: ArrayList<String>, val context: Context) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.player_card,
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
                            changeName(newName,oldName)
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
        val name = view.tv_simple_card
        val pencil = view.iv_pencil



    }

    fun changeName(newName: String,oldName: String){
        WaitingGame.playerName = newName
        playerList[playerList.indexOf(oldName)] = newName
        var db = FirebaseFirestore.getInstance()
        var gameRef = db.collection("games").document(NewGameActivity.ACCESS_CODE)
        gameRef.update("playerList", playerList)

    }

}






