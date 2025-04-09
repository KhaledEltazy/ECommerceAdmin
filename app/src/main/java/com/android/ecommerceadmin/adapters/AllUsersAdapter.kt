package com.android.ecommerceadmin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.data.User
import com.android.ecommerceadmin.databinding.AllUsersItemBinding
import com.bumptech.glide.Glide

class AllUsersAdapter : RecyclerView.Adapter<AllUsersAdapter.AllUserViewHolder>() {

    var onClickedItem : ((User) -> Unit)? = null

    inner class AllUserViewHolder(private val binding : AllUsersItemBinding) :
            RecyclerView.ViewHolder(binding.root){

                fun bind(user : User){
                    binding.apply {
                        nameTvProfileFragment.text = "${user.firstName} ${user.lastName}"
                        emailTV.text = user.email
                        Glide.with(itemView).load(user.img!!).error(
                            R.drawable.baseline_person_24)
                            .into(imageNameProfileFragment)
                    }
                }
            }

    private val diffUtil = object : DiffUtil.ItemCallback<com.android.ecommerceadmin.data.User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUserViewHolder {
        return AllUserViewHolder(AllUsersItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AllUserViewHolder, position: Int) {
        val currentUser = differ.currentList[position]

        holder.bind(currentUser)

        holder.itemView.setOnClickListener {
            onClickedItem?.invoke(currentUser)
        }
    }
}