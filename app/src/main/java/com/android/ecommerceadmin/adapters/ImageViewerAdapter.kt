package com.android.ecommerceadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.databinding.ImageItemBinding
import com.bumptech.glide.Glide

class ImageViewerAdapter : RecyclerView.Adapter<ImageViewerAdapter.ImageViewHolder>() {
    //handling image selection
    var selectedImage : ((String) -> Unit)? = null
    //handling delete btn
    var clickedOnDeleteBtn : ((Int) -> Unit)? = null
    // handling item position
    var imageSelectedPosition = -1

    fun resetImageSelectedPosition() {
        imageSelectedPosition = -1
        notifyDataSetChanged()
    }

    //submit list
    fun setupImageList(images : List<String>){
        //create new list avoiding Ui not Updated
        differ.submitList(images.toList())
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(private val binding : ImageItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(image : String,position: Int){
            Glide.with(itemView).load(image).into(binding.productImageIV)
            if(imageSelectedPosition == position){
                binding.selectiveImageIV.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.VISIBLE
            } else {
                binding.selectiveImageIV.visibility = View.INVISIBLE
                binding.deleteBtn.visibility = View.GONE
            }

            binding.deleteBtn.setOnClickListener {
                clickedOnDeleteBtn?.invoke(position)
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

    private val differ = AsyncListDiffer(this,differUtil)


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
            if(imageSelectedPosition == holder.adapterPosition){
                // Deselect previous
                notifyItemChanged(imageSelectedPosition)
                imageSelectedPosition = -1
            } else {
                if(imageSelectedPosition >= 0)
                    // Update previous selection
                    notifyItemChanged(imageSelectedPosition)

                imageSelectedPosition = holder.adapterPosition
                // Update new selection
                notifyItemChanged(imageSelectedPosition)
                selectedImage?.invoke(currentImage)
            }
        }
    }

}