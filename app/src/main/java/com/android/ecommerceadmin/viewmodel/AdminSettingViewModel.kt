package com.android.ecommerceadmin.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.ECommerceAdminApplication
import com.android.ecommerceadmin.data.User
import com.android.ecommerceadmin.util.CloudinaryApi
import com.android.ecommerceadmin.util.RegisterValidation
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class AdminSettingViewModel@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val cloudinaryApi: CloudinaryApi,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Undefined())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Undefined())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
            try {
                val userSnapshot = firestore.collection("user").document(auth.uid!!).get().await()
                val user = userSnapshot.toObject(User::class.java)
                user?.let { _user.emit(Resource.Success(it)) }
            } catch (e: Exception) {
                _user.emit(Resource.Error(e.message ?: "Failed to load user data"))
            }
        }
    }

    fun updateUser(user: User, imageUri: Uri?) {
        if (!isInputValid(user)) {
            viewModelScope.launch { _updateInfo.emit(Resource.Error("Check your inputs")) }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())

            try {
                val imageUrl = imageUri?.let { uploadImageToCloudinary(it) }
                val updatedUser = user.copy(img = imageUrl ?: user.img)
                saveUserInformation(updatedUser)
            } catch (e: Exception) {
                _updateInfo.emit(Resource.Error(e.message ?: "Update failed"))
            }
        }
    }

    private suspend fun uploadImageToCloudinary(imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val imageBitmap = MediaStore.Images.Media.getBitmap(
                getApplication<ECommerceAdminApplication>().contentResolver,
                imageUri
            )
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()

            cloudinaryApi.uploadImageByteArray(imageByteArray) // Upload and return the image URL
        }
    }

    private suspend fun saveUserInformation(user: User) {
        try {
            firestore.collection("user").document(auth.uid!!).set(user).await()
            _updateInfo.emit(Resource.Success(user))
        } catch (e: Exception) {
            _updateInfo.emit(Resource.Error(e.message ?: "Failed to update user info"))
        }
    }

    private fun isInputValid(user: User): Boolean {
        return validateEmail(user.email) is RegisterValidation.Success &&
                user.firstName.trim().isNotEmpty() &&
                user.lastName.trim().isNotEmpty()
    }

    fun logout(){
        auth.signOut()
    }

}