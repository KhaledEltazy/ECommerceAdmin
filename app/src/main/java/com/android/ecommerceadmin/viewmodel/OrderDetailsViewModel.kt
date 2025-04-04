package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Order
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
    private val _confirmOrder = MutableStateFlow<Resource<Order>>(Resource.Undefined())
    val confirmOrder = _confirmOrder.asSharedFlow()


    fun changeTheStateOfOrder(orderId: Long, newState: String) {
        viewModelScope.launch {
            _confirmOrder.emit(Resource.Loading())
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
                                _confirmOrder.emit(Resource.Success(updatedOrder!!)) // Emit updated order
                            }
                        }
                        .addOnFailureListener {
                            viewModelScope.launch {
                                _confirmOrder.emit(Resource.Error(it.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _confirmOrder.emit(Resource.Error("Order not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _confirmOrder.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}