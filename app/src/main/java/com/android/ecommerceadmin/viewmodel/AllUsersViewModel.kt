package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.User
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllUsersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _allUsers = MutableStateFlow<Resource<List<User>>>(Resource.Undefined())
    val allUsers = _allUsers.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            _allUsers.emit(Resource.Loading())
        }
        firestore.collection("user").get()
            .addOnSuccessListener {
                val users = it.toObjects(User::class.java)
                viewModelScope.launch {
                    _allUsers.emit(Resource.Success(users))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _allUsers.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}