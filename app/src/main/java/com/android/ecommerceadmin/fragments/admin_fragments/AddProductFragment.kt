package com.android.ecommerceadmin.fragments.admin_fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.ColorAdapter
import com.android.ecommerceadmin.adapters.ImageViewerAdapter
import com.android.ecommerceadmin.databinding.FragmentAddProductBinding
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.viewmodel.AddProductViewmodel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private lateinit var binding : FragmentAddProductBinding
    //instance from viewModel
    private val addProductViewModel by viewModels<AddProductViewmodel>()
    //colorAdapter and ImageAdapter
    private val colorAdapter by lazy{ ColorAdapter()}
    private val imageAdapter by lazy {ImageViewerAdapter()}

    //initialize variables
    val productName = binding.etName.text.toString().trim()
    val category = binding.category.text.toString().trim()
    val priceStr = binding.etPrice.text.toString().trim()
    val offerStr = binding.etOffer.text.toString().trim()
    val description = binding.etProductDescription.text.toString().trim()
    val sizes = getSizesList(binding.etSizes.text.toString().trim())
    val stock = binding.etStock.text.toString().trim()
    private var selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()
    val dropMenuArray = resources.getStringArray(R.array.drop_menu_list)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateDropMenu()
        setupColorRv()
        setupImageRv()
        checkingColorRv()
        checkingImagesRv()

        //observe value of category
        addProductViewModel.category.observe(requireActivity(), Observer { newText->
            if(binding.category.text.toString() != newText){
                binding.category.setText(newText,false)
            }
        })

        //collect the date of saveProduct
        lifecycleScope.launch {
            addProductViewModel.saveProduct.collect{
                when(it){
                    is Resource.Loading ->{
                        showingProgressBar()
                    }
                    is Resource.Success ->{
                        resetFields()
                        hidingProgressBar()
                        Snackbar.make(requireView(),resources.getString(R.string.snackBar_add_product),Snackbar.LENGTH_LONG)
                            .setAction(resources.getString(R.string.save)){
                                //do Nothing
                            }.show()
                    }
                    is Resource.Error->{
                        hidingProgressBar()
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG)
                            .setAction(resources.getString(R.string.back)){
                                //do nothing
                            }.show()
                    }
                    else -> Unit
                }
            }
        }

    }

    //implement dropDownMenu
    fun populateDropMenu(){
        val adapter = ArrayAdapter(requireActivity(), R.layout.drop_down_list,dropMenuArray)
        binding.apply {
            category.setAdapter(adapter)
            category.setOnItemClickListener{parent,_,position,_->
                val selectedItem = dropMenuArray[position]
                addProductViewModel.setCategory(selectedItem)
            }
        }
    }

    //handling progressBar
    private fun hidingProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }
    private fun showingProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun getSizesList(sizes: String): List<String>? {
        return if (sizes.isEmpty()) null else sizes.split(",")
    }

    private fun resetFields(){
        binding.apply {
            etName.text.clear()
            binding.etPrice.text.clear()
            binding.rvImages.visibility = View.INVISIBLE
            selectedImages.clear()
            selectedColors.clear()
            binding.etProductDescription.text.clear()
            binding.etSizes.text.clear()
            binding.rvColors.visibility = View.INVISIBLE
            binding.etStock.text.clear()
        }
    }

    //implement RecyclerView OF coloring
    fun setupColorRv(){
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    //implement RecyclerView OF Images
    fun setupImageRv(){
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    //hiding or showing colorRecyclerView
    fun checkingColorRv(){
        if(selectedColors.isEmpty())
            binding.rvColors.visibility = View.GONE
        else
            binding.rvColors.visibility = View.VISIBLE
    }

    //hiding or showing colorRecyclerView
    fun checkingImagesRv(){
        if(selectedImages.isEmpty())
            binding.rvImages.visibility = View.GONE
        else
            binding.rvImages.visibility = View.VISIBLE
    }
}