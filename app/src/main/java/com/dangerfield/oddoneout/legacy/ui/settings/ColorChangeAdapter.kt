package com.dangerfield.oddoneout.legacy.ui.settings

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.dangerfield.oddoneout.R
import com.dangerfield.oddoneout.databinding.ItemChangeColorBinding

class ColorChangeAdapter(
    var colors: List<ColorButton>,
    private var context: Context?,
    val mCallback: ColorChanger
) : RecyclerView.Adapter<ColorChangeAdapter.ViewHolder>() {

    var selectedPosition = -1

    inner class ViewHolder(binding: ItemChangeColorBinding) : RecyclerView.ViewHolder(binding.root) {
        var background: View = binding.changeColorBackground
        var hiddenText: TextView = binding.tvChangeColorType
        var colorChangeFilter: View = binding.colorChangeFilter
        var colorChangeCheckAnimation: LottieAnimationView = binding.colorChangeCheckAnimation

        init {
            context = binding.root.context

            binding.root.setOnClickListener {
                // first, unselect the previous
                if (selectedPosition != -1) { colors[selectedPosition].isSelected = false }
                // now select this one
                colors[adapterPosition].isSelected = true
                selectedPosition = adapterPosition
                if (colors[adapterPosition].color != Color.WHITE) {
                    mCallback.onColorChange((colors[adapterPosition]))
                }
                notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChangeColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (colors[position].color == Color.WHITE) {
            holder.hiddenText.visibility = View.VISIBLE
            holder.hiddenText.text = context!!.resources.getString(R.string.change_theme_random_colors)
        } else {
            holder.background.setBackgroundColor(colors[position].color)
            holder.hiddenText.visibility = View.INVISIBLE
        }
        if (colors[position].isSelected) {
            select(holder)
        } else {
            unselect(holder)
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    private fun unselect(holder: ViewHolder) {
        // unselect
        holder.colorChangeFilter.visibility = View.INVISIBLE
        holder.colorChangeCheckAnimation.visibility = View.INVISIBLE
    }

    private fun select(holder: ViewHolder) {
        // select
        holder.colorChangeFilter.visibility = View.VISIBLE
        holder.colorChangeCheckAnimation.speed = 2.0f
        holder.colorChangeCheckAnimation.visibility = View.VISIBLE
        holder.colorChangeCheckAnimation.playAnimation()
    }

    interface ColorChanger { fun onColorChange(colorButton: ColorButton) }
}
