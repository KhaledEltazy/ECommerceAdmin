package com.android.ecommerceadmin.data

data class Product(
    val id : String,
    val productName : String,
    val category : String,
    val price : Float,
    val offer: Float? = null,
    val productDescription : String? = null,
    val color : List<Int>? = null,
    val sizes : List<String>? = null,
    val image : List<String>,
    val stock : Int,
    val salesFrequency : Int = 0
) {
    constructor() : this("","","",0f,null,null,null,null, emptyList(),0,0)
}