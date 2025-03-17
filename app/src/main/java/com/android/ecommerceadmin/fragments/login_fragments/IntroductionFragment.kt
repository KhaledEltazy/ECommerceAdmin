package com.android.ecommerceadmin.fragments.login_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.activities.AdminActivity
import com.android.ecommerceadmin.databinding.FragmentIntroductionBinding
import com.android.ecommerceadmin.util.Constant.ACCOUNT_OPTION_FRAGMENT
import com.android.ecommerceadmin.util.Constant.SHOPPING_ACTIVITY
import com.android.ecommerceadmin.viewmodel.IntroductionViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class IntroductionFragment : Fragment() {

    private lateinit var binding: FragmentIntroductionBinding
    private val introViewmodel by viewModels<IntroductionViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //handling start button
        binding.apply {
            btnStart.setOnClickListener {
                introViewmodel.startButtonClicked()
                findNavController().navigate(R.id.action_introductionFragment_to_loginFragment)
            }
        }

        lifecycleScope.launch {
            introViewmodel.navigate.collect {
                when (it) {
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), AdminActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTION_FRAGMENT -> {
                        findNavController().navigate(R.id.action_introductionFragment_to_loginFragment)
                    }

                    else -> Unit
                }
            }
        }
    }
}