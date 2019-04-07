package com.dangerfield.spyfall

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.player_card.view.*

class PlayerAdapter(val playerList: ArrayList<String>, val context: Context) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.player_card,parent,false))
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.number?.text = (position + 1).toString()
        holder.name?.text = playerList[position]
        if(holder.name.text == "Elijah") holder.pencil.visibility = View.VISIBLE


    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val number = view.tv_player_number
        val name = view.tv_player_name
        val pencil = view.iv_pencil



    }


}
