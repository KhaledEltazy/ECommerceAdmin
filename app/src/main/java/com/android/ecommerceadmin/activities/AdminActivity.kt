package com.android.ecommerceadmin.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.android.ecommerceadmin.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}