package com.android.ecommerceadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.databinding.ImageItemBinding
import com.bumptech.glide.Glide

class ImageViewerAdapter : RecyclerView.Adapter<ImageViewerAdapter.ImageViewHolder>() {
    //handling image selection
    var selectedImage : ((String) -> Unit)? = null
    private var imageSelectedPosition = -1
    fun setupImageList(images : List<String>){
        differ.submitList(images)
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(private val binding : ImageItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(image : String,position: Int){
            Glide.with(itemView).load(image).into(binding.productImageIV)
            if(imageSelectedPosition == position){
                binding.selectiveImageIV.visibility = View.VISIBLE
            } else {
                binding.selectiveImageIV.visibility = View.INVISIBLE
            }
        }
    }

        private val differUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return newItem == oldItem
            }
        }

        val differ = AsyncListDiffer(this,differUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ImageItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentImage = differ.currentList[position]

        holder.bind(currentImage,position)

        holder.itemView.setOnClickListener {
            if(imageSelectedPosition == position){
                notifyItemChanged(imageSelectedPosition)
                imageSelectedPosition = -1
            } else {
                if(imageSelectedPosition >= 0)
                    notifyItemChanged(imageSelectedPosition)
                imageSelectedPosition = holder.adapterPosition
                selectedImage?.invoke(currentImage)
            }
        }
    }

}