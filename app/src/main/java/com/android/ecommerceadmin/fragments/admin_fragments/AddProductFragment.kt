package com.android.ecommerceadmin.fragments.admin_fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.ColorAdapter
import com.android.ecommerceadmin.adapters.ImageViewerAdapter
import com.android.ecommerceadmin.databinding.FragmentAddProductBinding
import com.android.ecommerceadmin.util.CloudinaryApi
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.viewmodel.AddProductViewModel
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
open class AddProductFragment : Fragment() {

    protected lateinit var binding: FragmentAddProductBinding

    protected val addProductViewModel by viewModels<AddProductViewModel>()

    protected val colorAdapter by lazy { ColorAdapter() }
    protected val imageAdapter by lazy { ImageViewerAdapter() }

    @Inject
    lateinit var cloudinaryApi: CloudinaryApi

    //variables of dropDownMenuArray
    protected lateinit var dropMenuArray: Array<String>

    //selectiveImage and list of array
    protected val selectedImages = mutableListOf<Uri>()
    protected val images = mutableListOf<String>()

    //selected ColorsList
    protected val selectedColors = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dropMenuArray = resources.getStringArray(R.array.drop_menu_list)
        populateDropMenu()
        setupColorRv()
        setupImageRv()
        checkingColorRv()

        // handling selecting images and showing imageRecyclerView
        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    selectedImages.clear()
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let { uri ->
                                selectedImages.add(uri)
                                images.add(uri.toString())
                                Log.d("TAG", "${selectedImages}")
                                Log.d("TAG", "${images}")
                            }
                        }
                    } else {
                        intent?.data?.let {
                            selectedImages.add(it)
                            images.add(it.toString())
                            Log.d("TAG", "${selectedImages}")
                            Log.d("TAG", "${images}")
                        }
                    }
                    imageAdapter.setupImageList(images)
                    checkingImagesRv()
                }
            }

        //handling delete btn for imageRecyclerView
        imageAdapter.clickedOnDeleteBtn = { position ->
            if (position !in images.indices) {
                // Prevent index out of bounds
            } else {

                images.removeAt(position)
                imageAdapter.notifyItemRemoved(position)

                imageAdapter.resetImageSelectedPosition()

                imageAdapter.setupImageList(images)
            }

        }

        //handling delete btn for imageRecyclerView
        colorAdapter.clickedOnDeleteBtn = { position ->
            selectedColors.removeAt(position)
            colorAdapter.resetColorSelectionPosition()
        }


        //handling color selection and btnColor
        binding.btnColors.setOnClickListener {
            ColorPickerDialog.Builder(requireActivity())
                .setTitle(R.string.select_color)
                .setPositiveButton("Save", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            colorAdapter.setupAdapterList(selectedColors)
                            checkingColorRv()
                        }
                    }
                })
                .setNegativeButton("Cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }
                .show()
        }

        //handling btnImages
        binding.btnImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                type = "image/*"
            }
            selectImagesActivityResult.launch(intent)
        }

        //handling saveProduct and saveBtn
        binding.fabSave.setOnClickListener {
            saveProduct()
        }

        collectSaveProductState()
    }

    // save Product function
    private fun saveProduct() {
        val productName = binding.etName.text.toString().trim()
        val category = binding.category.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val offerStr = binding.etOffer.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val sizes = getSizesList(binding.etSizes.text.toString().trim())
        val stockValue = binding.etStock.text.toString()

        if (productName.isEmpty() || category.isEmpty() || priceStr.isEmpty() || selectedImages.isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_fill_require_text, Toast.LENGTH_SHORT)
                .show()
            return
        }

        showingProgressBar()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uploadedImages =
                    selectedImages.map { cloudinaryApi.uploadImage(it, requireContext()) }
                withContext(Dispatchers.Main) {
                    addProductViewModel.saveProduct(
                        productName, category, priceStr.toFloat(),
                        offerStr.toFloatOrNull(), description.ifEmpty { null },
                        if (selectedColors.isEmpty()) null else selectedColors,
                        sizes, uploadedImages, stockValue.toInt()
                    )
                }
            } catch (e: Exception) {
                Log.e("Save Product Error", "Failed to save product", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to save product.", Toast.LENGTH_SHORT)
                        .show()
                    hidingProgressBar()
                }
            }
        }
    }

    private fun collectSaveProductState() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addProductViewModel.saveProduct.collect {
                    when (it) {
                        is Resource.Loading -> showingProgressBar()
                        is Resource.Success -> {
                            resetFields()
                            hidingProgressBar()
                            Snackbar.make(
                                requireView(),
                                R.string.snackBar_add_product,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        is Resource.Error -> {
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .addCallback(object : Snackbar.Callback() {
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
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

    private fun populateDropMenu() {
        val adapter = ArrayAdapter(requireContext(), R.layout.drop_down_list, dropMenuArray)
        binding.category.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                addProductViewModel.setCategory(dropMenuArray[position])
            }
        }
    }

    protected fun showingProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    protected fun hidingProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    protected fun getSizesList(sizes: String): List<String>? =
        sizes.takeIf { it.isNotEmpty() }?.split(",")

    private fun resetFields() {
        binding.apply {
            etName.text.clear()
            etPrice.text.clear()
            rvImages.visibility = View.INVISIBLE
            selectedImages.clear()
            selectedColors.clear()
            etProductDescription.text.clear()
            etSizes.text.clear()
            rvColors.visibility = View.INVISIBLE
            etStock.text.clear()
        }
    }

    private fun setupColorRv() {
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupImageRv() {
        binding.rvImages.apply {
            adapter = imageAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    protected fun checkingColorRv() {
        binding.rvColors.visibility = if (selectedColors.isEmpty()) View.GONE else View.VISIBLE
    }

    protected fun checkingImagesRv() {
        binding.rvImages.visibility = if (selectedImages.isEmpty()) View.GONE else View.VISIBLE
    }


}