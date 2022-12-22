package com.dangerfield.spyfall.legacy.ui.newGame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.legacy.models.GamePack
import kotlinx.android.synthetic.main.item_pack.view.*
import java.util.ArrayList

class PacksAdapter(var packs: ArrayList<GamePack>) : RecyclerView.Adapter<PacksAdapter.PackHolder>() {

    inner class PackHolder(view: View) : RecyclerView.ViewHolder(view) {
        var number: TextView = view.findViewById(R.id.tv_pack_number)
        var packType: TextView = view.findViewById(R.id.tv_pack_type)
        var background: ConstraintLayout = view.findViewById(R.id.card_background)

        init {
            view.setOnClickListener { select(view,packs[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackHolder {
        val singleButton = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pack, parent, false)
        return PackHolder(singleButton)
    }

    override fun onBindViewHolder(holder: PackHolder, position: Int) {

        val item = packs[position]
        holder.number.text = item.number.toString()
        holder.packType.text = item.type
        holder.background.setBackgroundColor(item.color)
    }

    private fun select(view: View, pack: GamePack){
        if(pack.isSelected){
            //unselect
            pack.isSelected = false
            view.view_pack_filter.visibility = View.INVISIBLE
            view.check_animation.visibility = View.INVISIBLE
        }
        else{
            //select
            pack.isSelected = true
            view.view_pack_filter.visibility = View.VISIBLE
            view.check_animation.visibility = View.VISIBLE
            view.check_animation.speed = 2.0f
            view.check_animation.playAnimation()
        }
    }

    override fun getItemCount(): Int { return packs.size }
}
