package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.AddressAdapter
import com.android.ecommerceadmin.adapters.OrdersAdapter
import com.android.ecommerceadmin.databinding.FragmentUserDetailsBinding
import com.android.ecommerceadmin.util.Constant.ORDER_DETAILS
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.util.VerticalItemDecoration
import com.android.ecommerceadmin.viewmodel.UserDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
    private val userDetailsViewModel by viewModels<UserDetailsViewModel>()


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
        Log.d("TAG",user.userId)
        setUpOrderRV()
        setUpAddressRV()

        binding.apply {
            userNameTv.text = "${user.firstName} ${user.lastName}"
            emailUserTv.text = user.email
            Glide.with(requireContext()).load(user.img ?: "").
            error(R.drawable.baseline_person_24).into(userImage)
        }

        //navigate or orderDetails
        ordersAdapter.onClicked ={
            val bundle = Bundle().apply {
                putParcelable(ORDER_DETAILS,it)
            }
            findNavController().navigate(R.id.action_userDetailsFragment_to_orderDetailsFragment,bundle)
        }

        userDetailsViewModel.fetchOrdersByUserId(user.userId)
        userDetailsViewModel.fetchAddressByUserId(user.userId)

        collectAddress()
        collectOrders()

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

    private fun showingOrderProgressBar(){
        binding.userOrderProgressbar.visibility = View.VISIBLE
    }

    private fun hidingOrderProgressBar(){
        binding.userOrderProgressbar.visibility = View.GONE
    }

    private fun showingAddressProgressBar(){
        binding.userAddressesProgressbar.visibility = View.VISIBLE
    }

    private fun hidingAddressProgressBar(){
        binding.userAddressesProgressbar.visibility =View.GONE
    }

    //collect userAddresses
    private fun collectAddress(){
        lifecycleScope.launch {
            userDetailsViewModel.userAddress.collect{
                when(it){
                    is Resource.Loading ->
                        showingAddressProgressBar()
                    is Resource.Success ->{
                        addressAdapter.differ.submitList(it.data)
                        if(it.data!!.isEmpty()){
                            binding.addressesRV.visibility = View.GONE
                            binding.noAddressTv.visibility = View.VISIBLE
                        } else {
                            binding.addressesRV.visibility = View.VISIBLE
                            binding.noAddressTv.visibility = View.GONE
                        }
                        hidingAddressProgressBar()
                    }
                    is Resource.Error ->{
                        hidingAddressProgressBar()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
    //collect userOrders
    private fun collectOrders(){
        lifecycleScope.launch {
            userDetailsViewModel.userOrders.collect{
                when(it){
                    is Resource.Loading ->
                        showingOrderProgressBar()
                    is Resource.Success ->{
                        ordersAdapter.differ.submitList(it.data)
                        if(it.data!!.isEmpty()){
                            binding.allOrderRV.visibility = View.GONE
                            binding.noOrdersTv.visibility = View.VISIBLE
                        } else {
                            binding.allOrderRV.visibility = View.VISIBLE
                            binding.noOrdersTv.visibility = View.GONE
                        }
                        hidingOrderProgressBar()
                    }
                    is Resource.Error ->{
                        hidingOrderProgressBar()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}