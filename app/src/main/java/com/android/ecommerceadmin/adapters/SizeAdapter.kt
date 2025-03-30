package com.android.ecommerce.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.databinding.SizeItemBinding

class SizeAdapter : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {
    private var sizeSelectedPosition =-1
    inner class SizeViewHolder(private val binding : SizeItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(size : String,position : Int){
            binding.sizeChecked.text = size
            if (position == sizeSelectedPosition){
                    binding.sizeShadow.visibility = View.VISIBLE
            } else{
                binding.sizeShadow.visibility = View.INVISIBLE
            }
        }
    }

    val differUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this,differUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder(SizeItemBinding.inflate(LayoutInflater.from(
            parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val currentSize = differ.currentList[position]

        holder.bind(currentSize,position)

        holder.itemView.setOnClickListener {
            if (sizeSelectedPosition >= 0)
                notifyItemChanged(sizeSelectedPosition)

            sizeSelectedPosition = holder.adapterPosition
            notifyItemChanged(sizeSelectedPosition)

            onSizeSelected?.invoke(currentSize)
        }
    }

    var onSizeSelected : ((String)-> Unit)? = null
}