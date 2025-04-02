package com.android.ecommerceadmin.adapters

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.data.Order
import com.android.ecommerceadmin.databinding.OrderItemBinding
import com.android.ecommerceadmin.util.Constant.CANCEL
import com.android.ecommerceadmin.util.Constant.CONFIRMED
import com.android.ecommerceadmin.util.Constant.DELIVERED
import com.android.ecommerceadmin.util.Constant.ORDERED
import com.android.ecommerceadmin.util.Constant.RETURNED
import com.android.ecommerceadmin.util.Constant.SHIPPED

class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding : OrderItemBinding) : RecyclerView.ViewHolder(binding.root){
        val resources = itemView.resources
        var colorDrawable : Drawable =ColorDrawable(resources.getColor(R.color.ordered))

        fun bind(currentOrders : Order){
            when(currentOrders.orderStatus){
                ORDERED ->{
                    colorDrawable =ColorDrawable(resources.getColor(R.color.ordered))
                }
                CANCEL ->{
                    colorDrawable =ColorDrawable(resources.getColor(R.color.cancel))
                }
                CONFIRMED ->{
                    colorDrawable =ColorDrawable(resources.getColor(R.color.confirmed))
                }
                SHIPPED ->{
                    colorDrawable = ColorDrawable(resources.getColor(R.color.shipped))
                }
                DELIVERED ->{
                    colorDrawable =ColorDrawable(resources.getColor(R.color.delivered))
                }
                RETURNED ->{
                    colorDrawable =ColorDrawable(resources.getColor(R.color.returned))
                }

                else -> Unit
            }

            binding.apply {
                tvOrderId.text = currentOrders.orderId.toString()
                tvOrderDate.text = currentOrders.date
                customerName.text = currentOrders.address.fullName
                imageOrderState.setImageDrawable(colorDrawable)
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return newItem.products == oldItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(OrderItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val currentOrders = differ.currentList[position]

        holder.bind(currentOrders)

        holder.itemView.setOnClickListener {
            onClicked?.invoke(currentOrders)
        }
    }

    var onClicked : ((Order)-> Unit)? = null

}
