package com.android.ecommerceadmin.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val id : String = "",
    var productName : String,
    var category : String,
    var price : Float,
    var offer : Float? = null,
    var productDescription : String? = null,
    var color : List<Int>? = null,
    var sizes : List<String>? = null,
    var images : List<String>,
    var stock : Int,
    var salesFrequency : Int = 0
) : Parcelable{
    constructor() : this("","","",0f,null,null,null,null, emptyList(),0,0)
}