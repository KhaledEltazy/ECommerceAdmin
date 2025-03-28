package com.android.ecommerceadmin.util

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryApi @Inject constructor() {
    private val cloudinary = Cloudinary(
        mapOf(
            //the codes inside fixedCode folder
            "cloud_name" to "",
            "api_key" to "",
            "api_secret" to ""
        )
    )

    suspend fun uploadImage(imageUri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val byteArray = inputStream?.readBytes() ?: throw Exception("Error reading image")
                val request = cloudinary.uploader().upload(byteArray, ObjectUtils.emptyMap())
                request["secure_url"] as String
            } catch (e: Exception) {
                throw Exception("Image upload failed: ${e.message}")
            }
        }
    }
}