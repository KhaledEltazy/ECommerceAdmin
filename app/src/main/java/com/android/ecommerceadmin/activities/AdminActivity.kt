package com.android.ecommerceadmin.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.android.ecommerceadmin.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedPreferences()
        setContentView(R.layout.activity_admin)

        if (intent.getBooleanExtra("navigateToOrders", false)) {
            val navController = findNavController(R.id.fragmentContainerView2)
            navController.navigate(R.id.ordersFragment)
        }

        // Save admin FCM token to Firestore
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FirebaseFirestore.getInstance()
                .collection("adminTokens")
                .document("mainAdmin")
                .set(mapOf("token" to token))
        }
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