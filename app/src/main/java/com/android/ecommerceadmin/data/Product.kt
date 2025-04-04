package com.android.ecommerceadmin.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val id : String,
    val productName : String,
    val category : String,
    val price : Float,
    val offer : Float? = null,
    val productDescription : String? = null,
    val color : List<Int>? = null,
    val sizes : List<String>? = null,
    val images : List<String>,
    var stock : Int,
    var salesFrequency : Int = 0
) : Parcelable{
    constructor() : this("","","",0f,null,null,null,null, emptyList(),0,0)
}