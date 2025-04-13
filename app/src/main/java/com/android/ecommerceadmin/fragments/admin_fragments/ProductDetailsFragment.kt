package com.android.ecommerceadmin.fragments.admin_fragments

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.adapters.SizeAdapter
import com.android.ecommerce.adapters.Viewpager2Adapter
import com.android.ecommerceadmin.adapters.ColorAdapter
import com.android.ecommerceadmin.databinding.FragmentProductDetailsBinding
import com.android.ecommerceadmin.helper.getProductPrice
import com.google.android.material.tabs.TabLayoutMediator

class ProductDetailsFragment : Fragment() {
    private lateinit var binding : FragmentProductDetailsBinding
    private val viewpager2Adapter by lazy { Viewpager2Adapter() }
    private val colorAdapter by lazy { ColorAdapter() }
    private val sizeAdapter by lazy { SizeAdapter() }
    private val args by  navArgs<ProductDetailsFragmentArgs>()
    private var selectedColor : Int? = null
    private var selectedSize : String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()
        setColorRecyclerView()
        setSizeRecyclerView()

        //receive selected Product
        val product = args.product

        //check if the product have color or not and submit color list to adapter
        if (product.color != null){
            colorAdapter.setupAdapterList(product.color!!)
        } else {
            binding.tvColorProductDetails.visibility = View.GONE
            binding.rvColor.visibility = View.GONE
        }

        //check if the product have size or not and submit sizes list to adapter
        if (product.sizes != null){
            sizeAdapter.differ.submitList(product.sizes)
        }else {
            binding.tvSizesProductDetails.visibility = View.GONE
            binding.rvSizes.visibility = View.GONE
        }

        //submit images list to viewpagerAdapter
        viewpager2Adapter.differ.submitList(product.images)
        //connect tabLayout to viewPager to show dots under photos
        TabLayoutMediator(binding.tabLayoutDots,binding.viewpagerProductImages){_,_->}.attach()

        //determine the selectedSize
        sizeAdapter.onSizeSelected ={
            selectedSize = it
        }

        //determine the selectedColor
        colorAdapter.onColorSelected= {
            selectedColor = it
        }


        binding.apply {

            ivClose.setOnClickListener {
                findNavController().navigateUp()
            }

            tvProductNameProductDetails.text = product.productName
            if (product.offer == null) {
                tvPriceProductDetails.text = "$ ${product.price}"
                tvOfferProductDetails.visibility = View.INVISIBLE
            } else {
                tvPriceProductDetails.text = "$ ${product.price}"
                tvPriceProductDetails.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvOfferProductDetails.text = "$ ${product.offer.getProductPrice(product.price)}"

            }

            if(product.productDescription != null)
                tvProductDescriptionProductDetails.text = product.productDescription
            else {
                tvProductDescriptionProductDetails.visibility = View.GONE
            }

        }

    }

    private fun setViewPager(){
        binding.viewpagerProductImages.apply {
            adapter = viewpager2Adapter
        }
    }

    private fun setColorRecyclerView(){
        binding.rvColor.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            adapter = colorAdapter
        }
    }

    private fun setSizeRecyclerView(){
        binding.rvSizes.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            adapter = sizeAdapter
        }
    }
}