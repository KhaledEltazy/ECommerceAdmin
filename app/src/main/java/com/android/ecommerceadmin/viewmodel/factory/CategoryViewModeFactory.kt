package com.android.ecommerceadmin.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.ecommerceadmin.data.Category
import com.android.ecommerceadmin.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModeFactory(
    val firestore: FirebaseFirestore,
    val category: Category
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore,category) as T

    }
}