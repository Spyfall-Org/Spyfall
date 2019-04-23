package com.dangerfield.spyfall.WaitingActivity

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
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.change_name.view.*
import kotlinx.android.synthetic.main.player_card.view.*

class PlayerAdapter(val playerName: String, val playerList: ArrayList<String>, val context: Context) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {


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
            holder.pencil.visibility = View.VISIBLE
            holder.pencil.setOnClickListener {view ->
                changeName(view)
            }
        }


    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val number = view.tv_player_number
        val name = view.tv_simple_card
        val pencil = view.iv_pencil



    }

    fun changeName(view: View){
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.change_name)

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
        dialog.show()
        }


    }






