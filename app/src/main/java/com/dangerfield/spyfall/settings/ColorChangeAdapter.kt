package com.dangerfield.spyfall.settings

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.item_change_color.view.*

class ColorChangeAdapter(var colors: List<ColorButton>, private var context: Context?) : RecyclerView.Adapter<ColorChangeAdapter.ViewHolder>() {


    var selectedPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var background: View = view.findViewById(R.id.change_color_background)

        init {
            context = view.context

            view.setOnClickListener {
                //first, unselect the previous
                if(selectedPosition != -1){ colors[selectedPosition].isSelected = false }
                //now select this one
                colors[adapterPosition].isSelected = true
                selectedPosition = adapterPosition

                notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleButton = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_change_color, parent, false)
        return ViewHolder(singleButton)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.background.setBackgroundColor(colors[position].color)
        if(colors[position].isSelected){
            select(holder.itemView)
        }else{
            unselect(holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    private fun unselect(view: View){
        //unselect
        view.color_change_filter.visibility = View.INVISIBLE
        view.color_change_check_animation.visibility = View.INVISIBLE
    }
    private fun select(view: View){
        //select
        view.color_change_filter.visibility = View.VISIBLE
        view.color_change_check_animation.visibility = View.VISIBLE
        view.color_change_check_animation.playAnimation()
    }
}