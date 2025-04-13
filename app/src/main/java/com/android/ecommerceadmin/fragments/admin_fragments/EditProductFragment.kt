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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EditProductFragment : AddProductFragment() {

    private val args by navArgs<EditProductFragmentArgs>()
    private lateinit var currentProduct: Product

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentProduct = args.product

        prefillProductData()
        setupRecyclerViews()
        binding.fabSave.setOnClickListener { checkAndEditProduct() }
        observeEditProductState()
    }

    private fun prefillProductData() {
        binding.apply {
            etName.setText(currentProduct.productName)
            category.setText(currentProduct.category, false)
            etPrice.setText(currentProduct.price.toString())
            etOffer.setText(currentProduct.offer?.toString() ?: "")
            etProductDescription.setText(currentProduct.productDescription ?: "")
            etSizes.setText(currentProduct.sizes?.joinToString(",") ?: "")
            etStock.setText(currentProduct.stock.toString())
        }

        images.clear()
        images.addAll(currentProduct.images)

        selectedImages.clear()
        selectedImages.addAll(currentProduct.images.map { Uri.parse(it) })

        selectedColors.clear()
        selectedColors.addAll(currentProduct.color ?: emptyList())
    }

    private fun setupRecyclerViews() {
        imageAdapter.setupImageList(images)
        colorAdapter.setupAdapterList(selectedColors)
        checkingImagesRv()
        checkingColorRv()
    }

    private fun checkAndEditProduct() {
        val productName = binding.etName.text.toString().trim()
        val category = binding.category.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val offerStr = binding.etOffer.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val sizesList = getSizesList(binding.etSizes.text.toString().trim())
        val stockStr = binding.etStock.text.toString().trim()

        if (productName.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_fill_require_text, Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toFloat()
        val offer = offerStr.toFloatOrNull()
        val stock = stockStr.toInt()

        val currentImages = currentProduct.images
        val updatedImages = (currentImages + selectedImages.map { it.toString() }).distinct()

        val noChangesMade =
            productName == currentProduct.productName &&
                    category == currentProduct.category &&
                    price == currentProduct.price &&
                    offer == currentProduct.offer &&
                    (description.ifEmpty { null } == currentProduct.productDescription) &&
                    sizesList!!.toList() == currentProduct.sizes.orEmpty().toList() &&
                    selectedColors.toList() == currentProduct.color.orEmpty().toList() &&
                    stock == currentProduct.stock &&
                    updatedImages == currentImages

        if (noChangesMade) {
            Toast.makeText(requireContext(), "No changes made.", Toast.LENGTH_SHORT).show()
            return
        }

        editProduct(productName, category, price, offer, description, sizesList, stock)
    }

    private fun editProduct(
        productName: String,
        category: String,
        price: Float,
        offer: Float?,
        description: String,
        sizes: List<String>?,
        stock: Int
    ) {
        showingProgressBar()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uploadedImages = selectedImages
                    .filter { uri -> uri.toString() !in currentProduct.images }
                    .map { uri -> cloudinaryApi.uploadImage(uri, requireContext()) }

                val finalImages = (currentProduct.images + uploadedImages).distinct()

                withContext(Dispatchers.Main) {
                    addProductViewModel.editProduct(
                        productId = currentProduct.id,
                        productName = productName,
                        category = category,
                        price = price,
                        offer = offer,
                        description = description.ifEmpty { null },
                        color = if (selectedColors.isEmpty()) null else selectedColors,
                        sizes = sizes,
                        images = finalImages,
                        stock = stock
                    )
                }
            } catch (e: Exception) {
                Log.e("EditProductFragment", "Failed to update product", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to update product.", Toast.LENGTH_SHORT).show()
                    hidingProgressBar()
                }
            }
        }
    }

    private fun observeEditProductState() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addProductViewModel.editProduct.collect { result ->
                    when (result) {
                        is Resource.Loading -> showingProgressBar()
                        is Resource.Success -> {
                            hidingProgressBar()
                            Snackbar.make(requireView(), "Product updated successfully", Snackbar.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(), result.message.toString(), Snackbar.LENGTH_LONG)
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