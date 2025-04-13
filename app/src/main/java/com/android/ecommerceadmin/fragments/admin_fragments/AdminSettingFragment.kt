package com.android.ecommerceadmin.fragments.admin_fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.activities.LoginActivity
import com.android.ecommerceadmin.data.User
import com.android.ecommerceadmin.databinding.FragmentAdminSettingBinding
import com.android.ecommerceadmin.dialog.setupBottomSheetDialog
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.viewmodel.AdminSettingViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminSettingFragment : Fragment() {
    private lateinit var binding : FragmentAdminSettingBinding
    private val viewModel by viewModels<AdminSettingViewModel>()
    private var imageUri : Uri? = null
    private lateinit var imageActivityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                imageUri = it.data?.data
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminSettingBinding.inflate(inflater)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //handle save button
        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.trim().toString()
                val lastName = edLastName.text.trim().toString()
                val email = edEmail.text.trim().toString()
                val user = User("",firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }

        binding.editImageLL.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }

        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {

            }
        }

        //hande close icon
        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }

        // setup navigation of logOut clickListener
        binding.logOutBtn.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(),LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        lifecycleScope.launch {
            viewModel.user.collect{
                when(it){
                    is Resource.Loading ->{
                        showUserLoading()
                    }
                    is Resource.Success->{
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error->{
                        hideUserLoading()
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateInfo.collect{
                when(it){
                    is Resource.Loading->{
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success->{
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        binding.buttonSave.revertAnimation()
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()

                    }
                    else -> Unit
                }
            }
        }


    }

    private fun showUserInformation(user: User) {
        binding.apply {
            edFirstName.setText(user.firstName)
            edLastName.setText(user.lastName)
            edEmail.setText(user.email)
            val uriString = user.img
           Glide.with(requireContext()).load(user.img ?: "").error(resources.getDrawable(R.drawable.baseline_person_24))
             .into(imageUser)
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }
}