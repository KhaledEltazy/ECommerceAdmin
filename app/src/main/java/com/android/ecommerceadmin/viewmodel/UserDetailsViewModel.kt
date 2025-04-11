package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Address
import com.android.ecommerceadmin.data.Order
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _userOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Undefined())
    val userOrders = _userOrders.asStateFlow()

    private val _userAddress = MutableStateFlow<Resource<List<Address>>>(Resource.Undefined())
    val userAddress = _userAddress.asStateFlow()

    //Get user orders using userId (document ID)
    fun fetchOrdersByUserId(userId: String) {
        viewModelScope.launch {
            _userOrders.emit(Resource.Loading())
        }

        firestore.collection("user")
            .document(userId)
            .collection("orders")
            .get()
            .addOnSuccessListener { snapshot ->
                val orders = snapshot.map { it.toObject(Order::class.java) }
                viewModelScope.launch {
                    _userOrders.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener { e ->
                viewModelScope.launch {
                    _userOrders.emit(Resource.Error(e.message ?: "Failed to fetch orders"))
                }
            }
    }

    //Get user address using userId (document ID)
    fun fetchAddressByUserId(userId: String) {
        viewModelScope.launch {
            _userAddress.emit(Resource.Loading())
        }

        firestore.collection("user")
            .document(userId)
            .collection("address")
            .get()
            .addOnSuccessListener { snapshot ->
                val addresses = snapshot.map { it.toObject(Address::class.java) }
                viewModelScope.launch {
                    _userAddress.emit(Resource.Success(addresses))
                }
            }
            .addOnFailureListener { e ->
                viewModelScope.launch {
                    _userAddress.emit(Resource.Error(e.message ?: "Failed to fetch addresses"))
                }
            }
    }
}