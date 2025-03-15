package com.android.ecommerceadmin.data

data class User(
    val firstName : String,
    val lastName : String,
    val email : String,
    val img : String? = ""
) {
    constructor() : this("","","","")
}