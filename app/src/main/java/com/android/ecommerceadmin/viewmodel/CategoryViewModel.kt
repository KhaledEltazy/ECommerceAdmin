package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Category
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    val fireStore: FirebaseFirestore,
    val category: Category
) : ViewModel() {
    private val _categoryProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val categoryProducts = _categoryProducts.asStateFlow()

    private val pagingCategoryProducts = PagingCategoryProducts()
    private var isFetchingCategoryProducts = false

    fun fetchCategoryProducts(category: Category) {
        if (!pagingCategoryProducts.isPagingEnd && !isFetchingCategoryProducts) {
            isFetchingCategoryProducts = true
            viewModelScope.launch {
                _categoryProducts.emit(Resource.Loading())
            }
            fireStore.collection("products")
                .whereEqualTo("category", category.category)
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

}

internal data class PagingCategoryProducts(
    var pageNumbers: Long = 1,
    var oldCategoryList: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)