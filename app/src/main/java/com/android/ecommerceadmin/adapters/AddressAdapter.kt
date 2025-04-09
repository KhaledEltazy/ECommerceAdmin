package com.android.ecommerceadmin.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.data.Address
import com.android.ecommerceadmin.databinding.UserAddressItemBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder( val binding : UserAddressItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address) {
            binding.apply {
                tvFullName.text = address.fullName
                tvAddress.text = "${address.street} / ${address.city}"
                tvPhoneNumber.text = address.phone
            }
        }
    }

    private val diffUntil = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this,diffUntil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(UserAddressItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentAddress = differ.currentList[position]

        holder.bind(currentAddress)
    }

}