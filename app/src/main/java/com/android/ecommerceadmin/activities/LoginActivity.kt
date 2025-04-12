package com.android.ecommerceadmin.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.ecommerceadmin.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedPreferences()
        setContentView(R.layout.activity_login)

    }

    private fun applySavedPreferences() {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val lang = sharedPref.getString("language", "en") ?: "en"
        val darkMode = sharedPref.getBoolean("dark_mode", false)

        // Apply Language
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Apply Dark Mode
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}