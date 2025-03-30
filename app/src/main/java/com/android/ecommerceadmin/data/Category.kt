package com.android.ecommerceadmin.data

import com.android.ecommerceadmin.util.Constant.ACCESSORY
import com.android.ecommerceadmin.util.Constant.CHAIR
import com.android.ecommerceadmin.util.Constant.CUPBOARD
import com.android.ecommerceadmin.util.Constant.FURNITURE
import com.android.ecommerceadmin.util.Constant.TABLE

sealed class Category(val category : String) {
    object Chair : Category(CHAIR)
    object Cupboard : Category(CUPBOARD)
    object Table : Category(TABLE)
    object Accessory : Category(ACCESSORY)
    object Furniture : Category(FURNITURE)
}