package com.android.ecommerceadmin.util

import android.content.Context
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.data.GridItem

class GridItemsList(context:Context) {
    val getGridItemList : List<GridItem> = listOf(
        GridItem(context.getString(R.string.add_product),R.drawable.add_new_products),
        GridItem(context.getString(R.string.all_products),R.drawable.all_products),
        GridItem(context.getString(R.string.orders),R.drawable.orders),
        GridItem(context.getString(R.string.all_users),R.drawable.users),
        GridItem(context.getString(R.string.reports),R.drawable.analytics),
        GridItem(context.getString(R.string.admin_settings),R.drawable.setting),
        )
}