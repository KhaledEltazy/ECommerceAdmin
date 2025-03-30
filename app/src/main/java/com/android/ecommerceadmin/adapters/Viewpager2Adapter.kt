package com.android.ecommerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.databinding.ImageViewPagerBinding
import com.bumptech.glide.Glide

class Viewpager2Adapter : RecyclerView.Adapter<Viewpager2Adapter.ViewPager2ViewHolder>() {
    inner class ViewPager2ViewHolder(private val binding : ImageViewPagerBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(image : String){
            Glide.with(itemView).load(image).into(binding.productImageViewPager)
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ViewHolder {
        return ViewPager2ViewHolder(ImageViewPagerBinding
            .inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPager2ViewHolder, position: Int) {
        val currentImage = differ.currentList[position]

        holder.bind(currentImage)
    }
}