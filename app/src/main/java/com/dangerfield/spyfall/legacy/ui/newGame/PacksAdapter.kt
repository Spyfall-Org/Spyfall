package com.dangerfield.spyfall.legacy.ui.newGame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.databinding.ItemPackBinding
import com.dangerfield.spyfall.legacy.models.GamePack

class PacksAdapter(var packs: ArrayList<GamePack>) : RecyclerView.Adapter<PacksAdapter.PackHolder>() {

    inner class PackHolder(binding: ItemPackBinding) : RecyclerView.ViewHolder(binding.root) {
        var number: TextView = binding.tvPackNumber
        var packType: TextView = binding.tvPackType
        var background: ConstraintLayout = binding.cardBackground

        init {
            binding.root.setOnClickListener { select(binding,packs[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackHolder {
        return PackHolder(
            ItemPackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PackHolder, position: Int) {
        val item = packs[position]
        holder.number.text = item.number.toString()
        holder.packType.text = item.type
        holder.background.setBackgroundColor(item.color)
    }

    private fun select(binding: ItemPackBinding, pack: GamePack){
        if(pack.isSelected){
            //unselect
            pack.isSelected = false
            binding.viewPackFilter.visibility = View.INVISIBLE
            binding.checkAnimation.visibility = View.INVISIBLE
        }
        else{
            //select
            pack.isSelected = true
            binding.viewPackFilter.visibility = View.VISIBLE
            binding.checkAnimation.visibility = View.VISIBLE
            binding.checkAnimation.speed = 2.0f
            binding.checkAnimation.playAnimation()
        }
    }

    override fun getItemCount(): Int { return packs.size }
}
