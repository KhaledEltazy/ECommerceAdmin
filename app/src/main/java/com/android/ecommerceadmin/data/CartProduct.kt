package com.android.ecommerceadmin.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity : Int,
    val selectedColor : Int? = null,
    val selectedSize : String? = null
) : Parcelable  {
    constructor() : this(Product(),0)
}