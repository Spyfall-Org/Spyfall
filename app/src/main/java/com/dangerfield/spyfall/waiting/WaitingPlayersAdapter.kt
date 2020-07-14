package com.dangerfield.spyfall.waiting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.goneIf
import kotlinx.android.synthetic.main.item_player_card.view.*

interface CurrentUserHelper {
    fun getCurrentUser() : String
}
class WaitingPlayersAdapter(private val currentUserHelper: CurrentUserHelper, private val nameHelper: ChangeNameHelper) : RecyclerView.Adapter<WaitingPlayersAdapter.ViewHolder>() {

    var players = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val number: TextView = view.tv_player_number
        val name: TextView = view.tv_new_game_name
        val pencil: ImageView = view.iv_pencil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_player_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number.text = (position + 1).toString()
        holder.name.text = players[position]
        holder.pencil.goneIf(holder.name.text.trim() != currentUserHelper.getCurrentUser().trim())
        holder.pencil.setOnClickListener { nameHelper.showNameChangeDialog(holder.itemView.context) }
    }

    override fun getItemCount(): Int { return players.size }
}
