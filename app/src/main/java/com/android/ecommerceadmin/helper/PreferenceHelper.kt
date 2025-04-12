package com.android.ecommerceadmin.helper

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceHelper @Inject constructor(
    private val sharedPref: SharedPreferences
) {

    fun isDarkMode(): Boolean = sharedPref.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) = sharedPref.edit().putBoolean("dark_mode", enabled).apply()

    fun isArabic(): Boolean = sharedPref.getString("language", "en") == "ar"
    fun setLanguage(arabic: Boolean) {
        val langCode = if (arabic) "ar" else "en"
        sharedPref.edit().putString("language", langCode).apply()
    }

    fun getLanguage(): String = sharedPref.getString("language", "en") ?: "en"
}
