package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.AddressAdapter
import com.android.ecommerceadmin.adapters.OrdersAdapter
import com.android.ecommerceadmin.databinding.FragmentUserDetailsBinding
import com.android.ecommerceadmin.util.VerticalItemDecoration
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {
    private lateinit var binding : FragmentUserDetailsBinding
    private val ordersAdapter by lazy {
        OrdersAdapter(false)
    }
    private val addressAdapter by lazy {
        AddressAdapter()
    }
    private val userArgs by navArgs<UserDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailsBinding.inflate(
            inflater,container,false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = userArgs.user
        setUpOrderRV()
        setUpAddressRV()

        binding.apply {
            userNameTv.text = "${user.firstName} ${user.lastName}"
            emailUserTv.text = user.email
            Glide.with(requireView()).load(user.img!!).
            error(R.drawable.baseline_person_24).into(userImage)
        }


    }

    private fun setUpOrderRV(){
        binding.allOrderRV.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setUpAddressRV(){
        binding.addressesRV.apply {
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

}