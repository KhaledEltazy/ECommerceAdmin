package com.android.ecommerceadmin.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.databinding.ColorItemBinding

class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    //handlingColorSelection
    var onColorSelected : ((Int) -> Unit)? = null
    //handling delete btn
    var clickedOnDeleteBtn : ((Int) -> Unit)? = null
    //handling color selection changes
    private var colorSelectionPosition = -1
    fun resetColorSelectionPosition(){
        colorSelectionPosition = -1
        notifyDataSetChanged()
    }
    fun setupAdapterList(list : List<Int>){
        differ.submitList(list)
        notifyDataSetChanged()
    }

    inner class ColorViewHolder(private val binding : ColorItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(color: Int,position: Int){
            //setColor
            val imageDrawable = ColorDrawable(color)
            binding.colorViewer.setImageDrawable(imageDrawable)
            //handling the visualise of selection
            if (position == colorSelectionPosition){
                binding.colorShadow.visibility = View.VISIBLE
                binding.colorChecked.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.VISIBLE
            } else {
                binding.colorShadow.visibility = View.INVISIBLE
                binding.colorChecked.visibility = View.INVISIBLE
                binding.deleteBtn.visibility = View.GONE
            }

            binding.deleteBtn.setOnClickListener {
                clickedOnDeleteBtn?.invoke(position)
            }
        }
    }

    private val differUtil = object : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,differUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(ColorItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val currentColor = differ.currentList[position]

        holder.bind(currentColor, position)

        holder.itemView.setOnClickListener {
            //handling selected item and disappear when re-click
            if (colorSelectionPosition == holder.adapterPosition) {
                // Deselect previous
                notifyItemChanged(colorSelectionPosition)
                colorSelectionPosition = -1
            } else {
                if (colorSelectionPosition >= 0)
                    // Update previous selection
                    notifyItemChanged(colorSelectionPosition)

                colorSelectionPosition = holder.adapterPosition
                // Update new selection
                notifyItemChanged(colorSelectionPosition)
                onColorSelected?.invoke(currentColor)
            }
        }
    }
}