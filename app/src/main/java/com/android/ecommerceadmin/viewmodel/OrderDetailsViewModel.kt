package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Order
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _orderState = MutableStateFlow<Resource<Order>>(Resource.Undefined())
    val orderState = _orderState.asSharedFlow()

    private val _deliveredOrder = MutableStateFlow<Resource<Product>>(Resource.Undefined())
    val deliveredOrder = _deliveredOrder.asSharedFlow()


    fun changeTheStateOfOrder(orderId: Long, newState: String) {
        viewModelScope.launch {
            _orderState.emit(Resource.Loading())
        }

        firestore.collection("orders")
            .whereEqualTo("orderId", orderId)  // Search for the order using orderId
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()  // Get the first matching order
                    document.reference.update("orderStatus", newState) // Update orderStatus
                        .addOnSuccessListener {
                            val updatedOrder = document.toObject(Order::class.java)?.copy(orderStatus = newState)
                            viewModelScope.launch {
                                _orderState.emit(Resource.Success(updatedOrder!!)) // Emit updated order
                            }
                        }
                        .addOnFailureListener {
                            viewModelScope.launch {
                                _orderState.emit(Resource.Error(it.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _orderState.emit(Resource.Error("Order not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _orderState.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    //changing salesFrequency and Stock Of products
    fun deliveredOrder(orderId: Long) {
        viewModelScope.launch {
            _deliveredOrder.emit(Resource.Loading())
        }

        firestore.collection("orders")
            .whereEqualTo("orderId", orderId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    val order = document.toObject(Order::class.java)

                    order?.products?.forEach { cartProduct ->
                        val productId = cartProduct.product.id
                        val quantity = cartProduct.quantity

                        firestore.collection("products")
                            .whereEqualTo("id", productId)
                            .get()
                            .addOnSuccessListener { productSnapshot ->
                                if (!productSnapshot.isEmpty) {
                                    val productDocument = productSnapshot.documents.first()
                                    val currentProduct = productDocument.toObject(Product::class.java)

                                    val newStock = (currentProduct?.stock ?: 0) - quantity
                                    val newSalesFrequency = (currentProduct?.salesFrequency ?: 0) + quantity

                                    productDocument.reference.update(
                                        mapOf(
                                            "stock" to newStock,
                                            "salesFrequency" to newSalesFrequency
                                        )
                                    ).addOnSuccessListener {
                                        val updatedProduct = cartProduct.product.copy(
                                            stock = newStock,
                                            salesFrequency = newSalesFrequency
                                        )

                                        viewModelScope.launch {
                                            _deliveredOrder.emit(Resource.Success(updatedProduct))
                                        }
                                    }.addOnFailureListener { e ->
                                        viewModelScope.launch {
                                            _deliveredOrder.emit(Resource.Error(e.message.toString()))
                                        }
                                    }
                                } else {
                                    viewModelScope.launch {
                                        _deliveredOrder.emit(Resource.Error("Product not found"))
                                    }
                                }
                            }
                    }
                } else {
                    viewModelScope.launch {
                        _deliveredOrder.emit(Resource.Error("Order not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _deliveredOrder.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}