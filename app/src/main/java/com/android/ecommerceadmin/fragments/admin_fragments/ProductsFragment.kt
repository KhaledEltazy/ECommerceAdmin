package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.ProductsAdapter
import com.android.ecommerceadmin.databinding.FragmentProductsBinding
import com.android.ecommerceadmin.util.Constant.ACCESSORY
import com.android.ecommerceadmin.util.Constant.ALL_PRODUCT
import com.android.ecommerceadmin.util.Constant.CHAIR
import com.android.ecommerceadmin.util.Constant.CUPBOARD
import com.android.ecommerceadmin.util.Constant.FURNITURE
import com.android.ecommerceadmin.util.Constant.PRODUCT_BUNDLE
import com.android.ecommerceadmin.util.Constant.TABLE
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.util.VerticalItemDecoration
import com.android.ecommerceadmin.viewmodel.CategoryViewModel
import com.android.ecommerceadmin.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsFragment : Fragment() {
    private lateinit var binding: FragmentProductsBinding
    private val productsAdapter by lazy {
        ProductsAdapter()
    }
    private val searchAdapter by lazy {
        ProductsAdapter()
    }
    private val searchViewmodel by viewModels<SearchViewModel>()
    var closeClicked: Boolean = true

    private val categoryViewmodel by viewModels<CategoryViewModel>()

    private val args by navArgs<ProductsFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //selected category
        val category = args.category

        //calling fun that implement searchRV
        setUpSearchRecyclerView()
        // calling fun that implement productsRv
        setUpProductsRecyclerView()
        //determine which category will appears
        selectCategory(category)

        //implementing search technique by control showing and hiding RecyclersViews during searches
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == "" || query == null) {
                    showingProductsRecyclerView()
                    hidingSearchProductRv()
                    closeClicked = true
                } else {
                    hidingProductsRecyclerView()
                    showingSearchProductsRv()
                    searchViewmodel.searchProducts(query)
                    closeClicked = false
                }
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == "" || newText == null) {
                    showingProductsRecyclerView()
                    hidingSearchProductRv()
                    closeClicked = true
                } else {
                    hidingProductsRecyclerView()
                    showingSearchProductsRv()
                    searchViewmodel.searchProducts(newText)
                    closeClicked = false
                }
                return true
            }
        })

        // Make entire search container clickable to activate SearchView
        binding.searchBarSF.setOnClickListener {
            binding.searchView.apply {
                isFocusable = true
                isIconified = false // Ensure SearchView is expanded
                requestFocus() // Show keyboard
            }
        }

        // Handle clear button (Close icon) when user clicks it
        binding.searchView.setOnCloseListener {
            if (!closeClicked) {
                showingProductsRecyclerView()
                hidingSearchProductRv()
                searchAdapter.differ.currentList.clear()
                binding.searchView.setQuery("", false) // Clear text
                binding.searchView.clearFocus() // Hide keyboard
                closeClicked = true
            }
            true
        }

        //handle seeProductButton
        searchAdapter.onSeeProductBtnClicked = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT_BUNDLE, it)
            }
            findNavController().navigate(
                R.id.action_productsFragment_to_productDetailsFragment,
                bundle
            )
        }

        //collect SearchResult
        collectSearchResult()
        // collect categoryProducts
        collectCategoryProducts()
        // collect allProducts
        collectAllProducts()

        //handling paging in recyclerView
        binding.productsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isLoading = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dy > 0 && !isLoading) {
                    isLoading = true
                    selectCategory(category)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isLoading = false
                }
            }
        })


    }

    private fun showingProductsRecyclerView() {
        binding.productsLayout.visibility = View.VISIBLE
    }

    private fun hidingProductsRecyclerView() {
        binding.productsLayout.visibility = View.GONE
    }

    private fun showingSearchProductsRv() {
        binding.searchRv.visibility = View.VISIBLE
    }

    private fun hidingSearchProductRv() {
        binding.searchRv.visibility = View.GONE
    }

    //implementing SearchRecyclerView
    fun setUpSearchRecyclerView() {
        binding.searchRv.apply {
            adapter = searchAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    //implementing ProductRecyclerView
    fun setUpProductsRecyclerView() {
        binding.productsRV.apply {
            adapter = productsAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    //collect values of searchResult
    fun collectSearchResult() {
        lifecycleScope.launch {
            searchViewmodel.searchResult.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.searchProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.searchProgressBar.visibility = View.GONE
                        searchAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        binding.searchProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    // collect values of categoryProducts
    fun collectCategoryProducts() {
        lifecycleScope.launch {
            categoryViewmodel.categoryProducts.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.productsProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.productsProgressBar.visibility = View.GONE
                        productsAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        binding.productsProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    // collect values of allyProducts
    fun collectAllProducts() {
        lifecycleScope.launch {
            categoryViewmodel.allProducts.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.productsProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.productsProgressBar.visibility = View.GONE
                        productsAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        binding.productsProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    fun selectCategory(category: String) {
        //select which page will appear
        when (category) {
            ALL_PRODUCT -> {
                categoryViewmodel.fetchAllProducts()
                binding.categoryTitle.text = ALL_PRODUCT
            }

            CHAIR -> {
                categoryViewmodel.fetchCategoryProducts(category)
                binding.categoryTitle.text = CHAIR
            }

            TABLE -> {
                categoryViewmodel.fetchCategoryProducts(category)
                binding.categoryTitle.text = TABLE
            }

            CUPBOARD -> {
                categoryViewmodel.fetchCategoryProducts(category)
                binding.categoryTitle.text = CUPBOARD
            }

            ACCESSORY -> {
                categoryViewmodel.fetchCategoryProducts(category)
                binding.categoryTitle.text = ACCESSORY
            }

            FURNITURE -> {
                categoryViewmodel.fetchCategoryProducts(category)
                binding.categoryTitle.text = FURNITURE
            }
        }
    }

}