package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Order
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    val firestore: FirebaseFirestore
) : ViewModel() {
    private val _getOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Undefined())
    val getOrders = _getOrders.asStateFlow()

    init {
        getAllOrders()
    }

    private val pagingOrders = PagingOrders()
    private var isPagingAllOrders: Boolean = false

    fun getAllOrders() {
        if (!pagingOrders.isPagingEnd && !isPagingAllOrders) {
            viewModelScope.launch {
                _getOrders.emit(Resource.Loading())
            }
            firestore.collection("orders")
                .limit(pagingOrders.pageNumbers * 5)
                .get()
                .addOnSuccessListener {
                    val orders = it.toObjects(Order::class.java)
                    pagingOrders.isPagingEnd = orders.size < 6 || orders.isEmpty()
                    pagingOrders.oldOrdersList = orders
                    pagingOrders.pageNumbers++
                    viewModelScope.launch {
                        _getOrders.emit(Resource.Success(orders))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _getOrders.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}

internal data class PagingOrders(
    var pageNumbers: Long = 1,
    var oldOrdersList: List<Order> = emptyList(),
    var isPagingEnd: Boolean = false
)