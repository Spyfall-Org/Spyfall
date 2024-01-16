package com.dangerfield.oddoneout.legacy.ui.waiting

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.oddoneout.databinding.ItemPlayerCardBinding
import com.dangerfield.oddoneout.legacy.util.goneIf

interface CurrentUserHelper {
    fun getCurrentUser() : String
}
class WaitingPlayersAdapter(username: String, private val nameHelper: ChangeNameHelper) : RecyclerView.Adapter<WaitingPlayersAdapter.ViewHolder>() {

    var players = ArrayList<String>()
        set(value) {
        field = value
        notifyDataSetChanged()
    }

    var currentUserName = username
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ItemPlayerCardBinding): RecyclerView.ViewHolder(binding.root) {
        val number: TextView = binding.tvPlayerNumber
        val name: TextView = binding.tvNewGameName
        val pencil: ImageView = binding.ivPencil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlayerCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number.text = (position + 1).toString()
        holder.name.text = players[position]
        holder.pencil.goneIf(holder.name.text.trim() != currentUserName.trim())
        holder.pencil.setOnClickListener { nameHelper.showNameChangeDialog(holder.itemView.context) }
    }

    override fun getItemCount(): Int { return players.size }
}
