package com.android.ecommerceadmin.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(
    val auth : FirebaseAuth,
    private val googleSignInClient : GoogleSignInClient
) : ViewModel() {
    private val _login = MutableStateFlow<Resource<FirebaseUser>>(Resource.Undefined())
    val login = _login.asSharedFlow()

    private val _googleLogin = MutableStateFlow<Resource<FirebaseUser>>(Resource.Undefined())
    val googleLogin = _googleLogin.asSharedFlow()

    private val _resetPassword = MutableStateFlow<Resource<String>>(Resource.Undefined())
    val resetPassword = _resetPassword.asSharedFlow()

    //firebase login Function
    fun loginAccount(email : String,password : String){
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            // this is not suitable way but in future i will make an extra attributes that check he is an admin or not
            if ( email == "khmeltazy@gmail.com") {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it))
                    }
                }
            } else {
                viewModelScope.launch {
                    _login.emit(Resource.Error("you are not an Admin"))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _login.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    //google login Function
    fun logInByGoogle(idToken : String){
        viewModelScope.launch {
            _googleLogin.emit(Resource.Loading())
        }
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            viewModelScope.launch {
                it.user?.let {
                    _googleLogin.emit(Resource.Success(it))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _googleLogin.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

    //handling ForgotPassword link
    fun resetPassword(email :String){
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Success(it.toString()))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}