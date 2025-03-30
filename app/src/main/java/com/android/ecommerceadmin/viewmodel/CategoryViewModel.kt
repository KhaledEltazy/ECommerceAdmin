package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    val fireStore: FirebaseFirestore
) : ViewModel() {
    private val _categoryProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val categoryProducts = _categoryProducts.asStateFlow()

    private val pagingCategoryProducts = PagingCategoryProducts()
    private var isFetchingCategoryProducts = false

    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val allProducts = _allProducts.asStateFlow()

    private val pagingAllProducts = PagingAllProducts()
    private var isFetchingAllProducts = false

    fun fetchCategoryProducts(category: String) {
        if (!pagingCategoryProducts.isPagingEnd && !isFetchingCategoryProducts) {
            isFetchingCategoryProducts = true
            viewModelScope.launch {
                _categoryProducts.emit(Resource.Loading())
            }
            fireStore.collection("products")
                .whereEqualTo("category",category)
                .limit(pagingCategoryProducts.pageNumbers * 4)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    pagingCategoryProducts.isPagingEnd = products.isEmpty() || products.size < 4
                    pagingCategoryProducts.oldCategoryList = products
                    pagingCategoryProducts.pageNumbers++
                    viewModelScope.launch {
                        _categoryProducts.emit(Resource.Success(products))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _categoryProducts.emit(Resource.Error(it.message.toString()))
                    }
                }.addOnCompleteListener {
                    isFetchingCategoryProducts = false
                }
        }
    }

    fun fetchAllProducts () {
        if (!pagingAllProducts.isPagingEnd && !isFetchingAllProducts) {
            isFetchingAllProducts = true
            viewModelScope.launch {
                _allProducts.emit(Resource.Loading())
            }
            fireStore.collection("products")
                .limit(pagingAllProducts.allProductsPage * 3)
                .get()
                .addOnSuccessListener {
                    val allProductsList = it.toObjects(Product::class.java)
                    pagingAllProducts.isPagingEnd = allProductsList.isEmpty() || allProductsList.size < 3
                    pagingAllProducts.oldAllProductsPage = allProductsList
                    pagingAllProducts.allProductsPage++
                    viewModelScope.launch {
                        _allProducts.emit(Resource.Success(allProductsList))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _allProducts.emit(Resource.Error(it.message.toString()))
                    }
                }.addOnCompleteListener {
                    isFetchingAllProducts = false
                }
        }
    }

}

internal data class PagingCategoryProducts(
    var pageNumbers: Long = 1,
    var oldCategoryList: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)

internal data class PagingAllProducts(
    var allProductsPage : Long = 1,
    var oldAllProductsPage : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false
)