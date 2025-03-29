package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.GridViewAdapter
import com.android.ecommerceadmin.data.GridItem
import com.android.ecommerceadmin.databinding.FragmentHomeAdminBinding
import com.android.ecommerceadmin.util.GridItemsList


class HomeAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeAdminBinding

    private lateinit var itemList : GridItemsList

    private lateinit var gridAdapter : GridViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = GridItemsList(requireContext())
        gridAdapter = GridViewAdapter(requireContext(),itemList.getGridItemList)
        binding.gvAdmin.adapter = gridAdapter

        //handle navigation of each icon
        binding.gvAdmin.setOnItemClickListener{_,_,position,_ ->
            val item = itemList.getGridItemList[position]
            when(item.title) {
                getString(R.string.add_product) ->
                    findNavController().navigate(R.id.action_homeAdminFragment_to_addProductFragment)
                getString(R.string.all_products) ->
                    findNavController().navigate(R.id.action_homeAdminFragment_to_categoriesFragment)
                    getString(R.string.orders) ->
                    Toast.makeText(requireContext(),"you Selected ${item.title}",Toast.LENGTH_SHORT).show()
                getString(R.string.push_notification) ->
                    Toast.makeText(requireContext(),"you Selected ${item.title}",Toast.LENGTH_SHORT).show()
                getString(R.string.all_users) ->
                    Toast.makeText(requireContext(),"you Selected ${item.title}",Toast.LENGTH_SHORT).show()
                getString(R.string.reports) ->
                    Toast.makeText(requireContext(),"you Selected ${item.title}",Toast.LENGTH_SHORT).show()
                getString(R.string.admin_settings) ->
                    Toast.makeText(requireContext(),"you Selected ${item.title}",Toast.LENGTH_SHORT).show()
            }
        }

    }
}