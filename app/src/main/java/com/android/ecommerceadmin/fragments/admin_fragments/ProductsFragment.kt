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
import androidx.recyclerview.widget.GridLayoutManager
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.ProductsAdapter
import com.android.ecommerceadmin.databinding.FragmentProductsBinding
import com.android.ecommerceadmin.util.Constant.PRODUCT_BUNDLE
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.util.VerticalItemDecoration
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

        //calling fun that implement searchRV
        setUpSearchRecyclerView()

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

    fun collectSearchResult(){
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

}