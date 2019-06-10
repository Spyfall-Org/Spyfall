package com.dangerfield.spyfall.game

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.item_simple_card.view.*

class GameViewsAdapter(val context: Context, list: ArrayList<String>) : RecyclerView.Adapter<GameViewsAdapter.ViewHolder>() {

    var items = ArrayList<String>()
        set(value) {
            field = value
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
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text = view.tv_simple_card_text

        init {
            view.setOnClickListener {

                if(text.paintFlags != Paint.STRIKE_THRU_TEXT_FLAG){
                    text.setTextColor(view.context.resources.getColor(R.color.colorLightGrey))
                    text.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    text.setTextColor(view.context.resources.getColor(R.color.colorPrimary))
                    text.paintFlags = 0
                }
            }
        }

    }



}
