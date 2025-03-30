package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.databinding.FragmentCategoriesBinding
import com.android.ecommerceadmin.util.Constant.ACCESSORY
import com.android.ecommerceadmin.util.Constant.ALL_PRODUCT
import com.android.ecommerceadmin.util.Constant.CATEGORY
import com.android.ecommerceadmin.util.Constant.CHAIR
import com.android.ecommerceadmin.util.Constant.CUPBOARD
import com.android.ecommerceadmin.util.Constant.FURNITURE
import com.android.ecommerceadmin.util.Constant.TABLE


class CategoriesFragment : Fragment() {
    private lateinit var binding : FragmentCategoriesBinding

    val bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            allProductLL.setOnClickListener {
                bundle.apply {
                    putString(CATEGORY,ALL_PRODUCT)
                }
                findNavController().navigate(R.id.action_categoriesFragment_to_productsFragment,bundle)
            }

            chairLL.setOnClickListener {
                bundle.apply {
                    putString(CATEGORY,CHAIR)
                }
                findNavController().navigate(R.id.action_categoriesFragment_to_productsFragment,bundle)
            }

            tableLL.setOnClickListener {
                bundle.apply {
                    putString(CATEGORY,TABLE)
                }
                findNavController().navigate(R.id.action_categoriesFragment_to_productsFragment,bundle)
            }

            accessoryLL.setOnClickListener {
                bundle.apply {
                    putString(CATEGORY, ACCESSORY)
                }
                findNavController().navigate(R.id.action_categoriesFragment_to_productsFragment,bundle)
            }

            cupboardLL.setOnClickListener {
                bundle.apply {
                    putString(CATEGORY,CUPBOARD)
                }
                findNavController().navigate(R.id.action_categoriesFragment_to_productsFragment,bundle)
            }

            furnitureLL.setOnClickListener {
                bundle.apply {
                    putString(CATEGORY, FURNITURE)
                }
                findNavController().navigate(R.id.action_categoriesFragment_to_productsFragment,bundle)
            }
        }
    }
}