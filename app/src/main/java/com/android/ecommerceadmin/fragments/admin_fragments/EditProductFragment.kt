package com.android.ecommerceadmin.fragments.admin_fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProductFragment : AddProductFragment() {

    private val args by navArgs<EditProductFragmentArgs>()
    private lateinit var currentProduct: Product

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load product details from arguments
        currentProduct = args.product

        // Pre-fill UI fields with existing product data
        binding.apply {
            etName.setText(currentProduct.productName)
            category.setText(currentProduct.category, false) // false = no filtering
            etPrice.setText(currentProduct.price.toString())
            etOffer.setText(currentProduct.offer?.toString() ?: "")
            etProductDescription.setText(currentProduct.productDescription ?: "")
            etSizes.setText(currentProduct.sizes?.joinToString(",") ?: "")
            etStock.setText(currentProduct.stock.toString())
        }

        // Populate images and colors
        images.addAll(currentProduct.images)
        selectedImages.clear() // clear any leftovers
        selectedImages.addAll(currentProduct.images.map { Uri.parse(it) }) // convert URLs to Uris
        selectedColors.addAll(currentProduct.color ?: emptyList())

        imageAdapter.setupImageList(images)
        colorAdapter.setupAdapterList(selectedColors)

        checkingImagesRv()
        checkingColorRv()

        // Change FAB button action to edit product instead of adding new
        binding.fabSave.setOnClickListener {
            editProduct()
        }

        collectEditProductState()
    }

    private fun editProduct() {
        val productName = binding.etName.text.toString().trim()
        val category = binding.category.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val offerStr = binding.etOffer.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val sizes = getSizesList(binding.etSizes.text.toString().trim())
        val stockValue = binding.etStock.text.toString().trim()

        if (productName.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_fill_require_text, Toast.LENGTH_SHORT).show()
            return
        }

        showingProgressBar()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Upload new images if selected
                val uploadedImages = selectedImages.map { cloudinaryApi.uploadImage(it, requireContext()) }
                val finalImages = (currentProduct.images + uploadedImages).distinct() // Keep old + new images

                withContext(Dispatchers.Main) {
                    addProductViewModel.editProduct(
                        id = currentProduct.id,
                        productName = productName,
                        category = category,
                        price = priceStr.toFloat(),
                        offer = offerStr.toFloatOrNull(),
                        description = description.ifEmpty { null },
                        color = if (selectedColors.isEmpty()) null else selectedColors,
                        sizes = sizes,
                        images = finalImages,
                        stock = stockValue.toInt()
                    )
                }
            } catch (e: Exception) {
                Log.e("Edit Product Error", "Failed to update product", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to update product.", Toast.LENGTH_SHORT).show()
                    hidingProgressBar()
                }
            }
        }
    }

    private fun collectEditProductState() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addProductViewModel.editProduct.collect {
                    when (it) {
                        is Resource.Loading -> showingProgressBar()
                        is Resource.Success -> {
                            hidingProgressBar()
                            Snackbar.make(requireView(), "Product updated successfully", Snackbar.LENGTH_LONG).show()
                            findNavController().popBackStack() // Navigate back after editing
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                                .addCallback(object : Snackbar.Callback() {
                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        hidingProgressBar()
                                    }
                                }).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}