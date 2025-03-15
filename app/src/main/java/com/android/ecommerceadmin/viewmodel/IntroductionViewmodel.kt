package com.android.ecommerceadmin.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.util.Constant.ACCOUNT_OPTION_FRAGMENT
import com.android.ecommerceadmin.util.Constant.SHARED_PREF_CHECKING_FIRST_OPEN
import com.android.ecommerceadmin.util.Constant.SHOPPING_ACTIVITY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewmodel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val auth: FirebaseAuth
) : ViewModel()  {
    private val _navigate = MutableSharedFlow<Int>()
    val navigate = _navigate.asSharedFlow()

    init {
        val isButtonClicked = sharedPreferences.getBoolean(SHARED_PREF_CHECKING_FIRST_OPEN,false)
        val user = auth.currentUser

        if(user != null){
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if(isButtonClicked) {
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        } else {
            Unit
        }
    }

    fun startButtonClicked(){
        sharedPreferences.edit().putBoolean(SHARED_PREF_CHECKING_FIRST_OPEN,true).apply()
    }
}