package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.OrderDetailsProductAdapter
import com.android.ecommerceadmin.databinding.FragmentOrderDetailsBinding
import com.android.ecommerceadmin.util.Constant.CANCEL
import com.android.ecommerceadmin.util.Constant.CONFIRMED
import com.android.ecommerceadmin.util.Constant.DELIVERED
import com.android.ecommerceadmin.util.Constant.ORDERED
import com.android.ecommerceadmin.util.Constant.RETURNED
import com.android.ecommerceadmin.util.Constant.SHIPPED
import com.android.ecommerceadmin.util.VerticalItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private val ordersDetailsArgs by navArgs<OrderDetailsFragmentArgs>()
    private val orderDetailsAdapter by lazy { OrderDetailsProductAdapter() }
    private var spinnerPosition: Int = 0
    private var isUserInteraction = false

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
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    // Handle order status change
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Enable listener only after layout is ready
        binding.orderStatusSpinner.viewTreeObserver.addOnGlobalLayoutListener {
            isUserInteraction = true
        }
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
}