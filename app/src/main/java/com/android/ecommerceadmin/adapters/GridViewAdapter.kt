package com.android.ecommerceadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.android.ecommerceadmin.data.GridItem
import com.android.ecommerceadmin.databinding.GridItemBinding

class GridViewAdapter(val context : Context, val items : List<GridItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): GridItem {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding : GridItemBinding

        val view = if(convertView == null){
            binding = GridItemBinding.inflate(LayoutInflater.from(context),parent,false)
        binding.root.apply { tag = binding }
        } else {
            binding = convertView.tag as GridItemBinding
            convertView
        }

        val item = getItem(position)
        binding.icon.setImageResource(item.drawable)
        binding.title.text = item.title

        return view
    }
}