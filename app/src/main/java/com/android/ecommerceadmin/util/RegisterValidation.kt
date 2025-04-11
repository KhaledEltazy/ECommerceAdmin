package com.android.ecommerceadmin.util

//it will be use to check email and password validations
sealed class RegisterValidation() {
    object Success : RegisterValidation()
    data class Failed(val message : String) : RegisterValidation()
}
    data class RegisterFieldsState(
        val email : RegisterValidation,
        val password : RegisterValidation
    )