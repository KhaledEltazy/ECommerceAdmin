package com.android.ecommerceadmin.di

import android.content.Context
import android.content.SharedPreferences
import com.android.ecommerceadmin.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFireStoreDatabase() = Firebase.firestore

//   @Provides
//   @Singleton
//   fun provideFirebaseCommon(
//       firestore : FirebaseFirestore,
//       firebaseAuth: FirebaseAuth
//   ) = FirebaseCommon(firestore,firebaseAuth)

    @Provides
    @Singleton
    fun provideGoogleSigInOption(@ApplicationContext context : Context) : GoogleSignInOptions{
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        gso: GoogleSignInOptions
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideFirebaseMessaging() : FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

//    // Provide Cloudinary Instance**
//    @Provides
//    @Singleton
//    fun provideCloudinaryApi(): CloudinaryApi {
//        return CloudinaryApi()
//    }

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context : Context): SharedPreferences{
        return context.getSharedPreferences("AppPrefs",Context.MODE_PRIVATE)
    }

}