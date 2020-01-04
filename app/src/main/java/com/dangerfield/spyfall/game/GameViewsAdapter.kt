package com.dangerfield.spyfall.game

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import kotlinx.android.synthetic.main.item_simple_card.*
import kotlinx.android.synthetic.main.item_simple_card.view.*
import kotlinx.android.synthetic.main.item_simple_card.view.ic_first

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
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_simple_card, parent, false))
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
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text = view.tv_simple_card_text
        val first_icon = view.ic_first
        val first_text = view.tv_first

        init {
            view.setOnClickListener {

                if(text.paintFlags != Paint.STRIKE_THRU_TEXT_FLAG){
                    text.setTextColor(view.context.resources.getColor(R.color.colorLightGrey))
                    text.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    text.setTextColor(view.context.resources.getColor(R.color.colorLightText))
                    text.paintFlags = 0
                }
            }
        }

    }



}
