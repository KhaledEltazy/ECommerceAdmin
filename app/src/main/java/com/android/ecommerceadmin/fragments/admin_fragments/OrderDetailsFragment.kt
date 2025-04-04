package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.OrderDetailsProductAdapter
import com.android.ecommerceadmin.data.Order
import com.android.ecommerceadmin.databinding.FragmentOrderDetailsBinding
import com.android.ecommerceadmin.util.Constant.CANCEL
import com.android.ecommerceadmin.util.Constant.CONFIRMED
import com.android.ecommerceadmin.util.Constant.DELIVERED
import com.android.ecommerceadmin.util.Constant.ORDERED
import com.android.ecommerceadmin.util.Constant.RETURNED
import com.android.ecommerceadmin.util.Constant.SHIPPED
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.util.VerticalItemDecoration
import com.android.ecommerceadmin.viewmodel.OrderDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private val ordersDetailsArgs by navArgs<OrderDetailsFragmentArgs>()
    private val orderDetailsAdapter by lazy { OrderDetailsProductAdapter() }
    private val orderDetailsViewModel by viewModels<OrderDetailsViewModel>()
    private var spinnerPosition: Int = 0
    private var isUserInteraction = false
    private var selectedItem = "Ordered"
    private lateinit var updatedOrder : Order

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val order = ordersDetailsArgs.orderDetails

        setOrderDetailsProductRv()

        // Set spinner position and setting up spinner
        when (order.orderStatus) {
            ORDERED -> spinnerPosition = 0
            CONFIRMED -> spinnerPosition = 1
            SHIPPED -> spinnerPosition = 2
            DELIVERED -> spinnerPosition = 3
            RETURNED -> spinnerPosition = 4
            CANCEL -> spinnerPosition = 5
        }
        setUpSpinner()

        //hiding confirmedBtn in cancel, Returned and delivered Orders
            if(order.orderStatus == DELIVERED || order.orderStatus == CANCEL || order.orderStatus == RETURNED){
                binding.confirmOrderBtn.visibility = View.GONE
                binding.confirmOrderText.text = "The Order Had been ${order.orderStatus}"
                binding.confirmOrderText.visibility = View.VISIBLE
            }


        binding.apply {
            tvOrderId.text = "Order ${order.orderId}"
            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = "$ ${order.totalPrice}"
            orderStatusSpinner.setSelection(spinnerPosition)
            imageCloseOrder.setOnClickListener { findNavController().navigateUp() }
        }

        orderDetailsAdapter.differ.submitList(order.products)

        // Handle Spinner Selection Changes
        binding.orderStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUserInteraction) {
                    selectedItem = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Enable listener only after layout is ready
        binding.orderStatusSpinner.viewTreeObserver.addOnGlobalLayoutListener {
            isUserInteraction = true
        }

        //handle ConfirmOrderBtn
        binding.confirmOrderBtn.setOnClickListener {
            if (selectedItem == order.orderStatus){
                Toast.makeText(requireContext(),"Please change Order Status",Toast.LENGTH_LONG).show()
            } else if(selectedItem == DELIVERED){
                orderDetailsViewModel.deliveredOrder(order.orderId)
                orderDetailsViewModel.changeTheStateOfOrder(order.orderId,selectedItem)
            }
            else {
                orderDetailsViewModel.changeTheStateOfOrder(order.orderId,selectedItem)
            }
        }


        collectOrderState()
        collectConfirmationOrder()
    }

    private fun setOrderDetailsProductRv() {
        binding.rvProducts.apply {
            adapter = orderDetailsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setUpSpinner() {
        val spinnerArray = resources.getStringArray(R.array.order_status_array)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.orderStatusSpinner.adapter = spinnerAdapter
    }

    private fun collectOrderState(){
        lifecycleScope.launch {
            orderDetailsViewModel.orderState.collect{
                when(it){
                    is Resource.Loading -> binding.orderDetailsProgressBar.visibility = View.VISIBLE
                    is Resource.Success ->{
                        binding.orderDetailsProgressBar.visibility = View.GONE
                        Snackbar.make(requireView(),"Order updated Successfully",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.orderDetailsProgressBar.visibility = View.GONE
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun collectConfirmationOrder(){
        lifecycleScope.launch {
            orderDetailsViewModel.deliveredOrder.collect{
                when(it){
                    is Resource.Loading -> binding.orderDetailsProgressBar.visibility = View.VISIBLE
                    is Resource.Success ->{
                        binding.orderDetailsProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"product changes stock and salesFrequency Successfully",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.orderDetailsProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }


 }