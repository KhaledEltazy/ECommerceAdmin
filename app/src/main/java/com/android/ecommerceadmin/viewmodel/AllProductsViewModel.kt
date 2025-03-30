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
class AllProductsViewModel @Inject constructor(
    val firestore: FirebaseFirestore
) : ViewModel() {
    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val allProducts = _allProducts.asStateFlow()

    private val pagingAllProducts = PagingAllProducts()
    private var isFetchingAllProducts = false

    fun fetchAllProducts () {
        if (!pagingAllProducts.isPagingEnd && !isFetchingAllProducts) {
            isFetchingAllProducts = true
            viewModelScope.launch {
                _allProducts.emit(Resource.Loading())
            }
            firestore.collection("products")
                .whereEqualTo("category", null)
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

internal data class PagingAllProducts(
    var allProductsPage : Long = 1,
    var oldAllProductsPage : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false
)