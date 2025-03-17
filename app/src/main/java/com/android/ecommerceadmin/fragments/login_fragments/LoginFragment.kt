package com.android.ecommerceadmin.fragments.login_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.activities.AdminActivity
import com.android.ecommerceadmin.databinding.FragmentLoginBinding
import com.android.ecommerceadmin.dialog.setupBottomSheetDialog
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.viewmodel.LoginViewmodel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val loginViewmodel by viewModels<LoginViewmodel>()

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account?.idToken?.let { idToken ->
                        loginViewmodel.logInByGoogle(idToken)
                    }
                } catch (e: ApiException) {
                    Toast.makeText(
                        requireContext(),
                        "${resources.getString(R.string.toast_google_signIn_failed)} ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().toLowerCase(Locale.ROOT).trim()
                val password = etPassword.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    loginViewmodel.loginAccount(email, password)
                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.toast_fill_missing_field),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            tvForgotPassword.setOnClickListener {
                setupBottomSheetDialog { email ->
                    loginViewmodel.resetPassword(email)
                }
            }

            //handle google signIn Button
            btnGoogle.setOnClickListener {
                val signInIntent = loginViewmodel.getGoogleSignInClient().signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        //collect login values
        lifecycleScope.launch {
            loginViewmodel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLogin.revertAnimation()
                        navigationToShoppingActivity()
                    }

                    is Resource.Error -> {
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        //collect resetPassword stateFlow
        lifecycleScope.launch {
            loginViewmodel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            resources.getString(R.string.snackBar_resetPassword),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            loginViewmodel.googleLogin.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnGoogle.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnGoogle.revertAnimation()
                        navigationToShoppingActivity()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.btnGoogle.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }

    }

    private fun navigationToShoppingActivity() {
        Intent(requireActivity(), AdminActivity::class.java).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.btnLogin.clearAnimation()
        binding.btnGoogle.clearAnimation()
    }
}