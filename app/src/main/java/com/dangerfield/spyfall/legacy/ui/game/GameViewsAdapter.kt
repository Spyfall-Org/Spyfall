package com.dangerfield.spyfall.legacy.ui.game

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.ItemSimpleCardBinding
import com.dangerfield.spyfall.legacy.util.UIHelper

class GameViewsAdapter(val context: Context, list: ArrayList<String>, firstPlayer: String?) : RecyclerView.Adapter<GameViewsAdapter.ViewHolder>() {

    var items = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var first = firstPlayer
    set(value){
        field = value
        //anytime the first player changes, aka player object list changes, grab a new first
        notifyDataSetChanged()
    }


    init {
        this.items = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSimpleCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position]

        if(first != null && items[position].trim() == first?.trim()){
            //then we are dealing with players
            holder.first_icon.background.setTint(UIHelper.accentColor)
            holder.first_text.visibility = View.VISIBLE
            holder.first_icon.visibility = View.VISIBLE
        }else {
            holder.first_text.visibility = View.INVISIBLE
            holder.first_icon.visibility = View.INVISIBLE
        }
    }

    class ViewHolder(binding: ItemSimpleCardBinding): RecyclerView.ViewHolder(binding.root) {
        val text = binding.tvSimpleCardText
        val first_icon = binding.icFirst
        val first_text = binding.tvFirst

        init {
            binding.root.setOnClickListener {

                if(text.paintFlags != Paint.STRIKE_THRU_TEXT_FLAG){
                    text.setTextColor(binding.root.context.resources.getColor(R.color.grey100))
                    text.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    text.setTextColor(binding.root.context.resources.getColor(R.color.colorLightText))
                    text.paintFlags = 0
                }
            }
        }

    }
}
