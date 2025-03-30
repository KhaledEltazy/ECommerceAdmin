package com.android.ecommerceadmin.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.databinding.ProductItemBinding
import com.android.ecommerceadmin.helper.getProductPrice
import com.bumptech.glide.Glide

class ProductsAdapter : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    var onSeeProductBtnClicked : ((Product) -> Unit)? = null
    var onEditProductBtnClicked : ((Product) -> Unit)? = null

    inner class ProductsViewHolder(private val binding : ProductItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.image[0]).into(prouctImageIV)
                productNameTV.text = product.productName
                priceTV.text = "$ ${product.price}"
                stockNumber.text = product.stock.toString()
                salesFrequency.text = product.salesFrequency.toString()
                if (product.offer == null) {
                    offerTV.visibility = View.INVISIBLE
                } else {
                    offerTV.text = "$ ${product.offer.getProductPrice(product.price)}"
                    priceTV.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }

                editProductBtn.setOnClickListener {
                    onEditProductBtnClicked?.invoke(product)
                }

                seeProductBtn.setOnClickListener {
                    onSeeProductBtnClicked?.invoke(product)
                }
            }
        }
    }
    
    private val differUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,differUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(ProductItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val currentProduct = differ.currentList[position]

        holder.bind(currentProduct)
    }

}