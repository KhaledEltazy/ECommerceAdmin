package com.android.ecommerceadmin.helper

fun Float?.getProductPrice(price : Float) : Float?{
    if (this == null) return price
    else {
        val remainingPricePercentage = 1f - this
        val priceAfterOffer = remainingPricePercentage * price
        return priceAfterOffer
    }
}