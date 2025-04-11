package com.android.ecommerceadmin.util

import android.util.Patterns

//checks if email is not empty and formatted successfully
fun validateEmail (email : String) : RegisterValidation{
    if(email.isEmpty()){
        return RegisterValidation.Failed("Email cannot be empty")
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        return RegisterValidation.Failed("Wrong email format")
    }
    return RegisterValidation.Success
}

//checks if password is not empty and formatted successfully
fun validatePassword(password : String) : RegisterValidation{
    if(password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")
    if(password.length <= 6)
        return RegisterValidation.Failed("Password should be contains 6 characters")
    else
        return RegisterValidation.Success
}