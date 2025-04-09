package com.android.ecommerceadmin.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val firstName : String,
    val lastName : String,
    val email : String,
    val img : String? = ""
) : Parcelable {
    constructor() : this("","","","")
}