package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.ViewModel
import com.android.ecommerceadmin.helper.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(preferenceHelper.isDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _isArabic = MutableStateFlow(preferenceHelper.isArabic())
    val isArabic: StateFlow<Boolean> = _isArabic

    fun toggleDarkMode(enabled: Boolean) {
        preferenceHelper.setDarkMode(enabled)
        _isDarkMode.value = enabled
    }

    fun toggleLanguage(arabic: Boolean) {
        preferenceHelper.setLanguage(arabic)
        _isArabic.value = arabic
    }
}
