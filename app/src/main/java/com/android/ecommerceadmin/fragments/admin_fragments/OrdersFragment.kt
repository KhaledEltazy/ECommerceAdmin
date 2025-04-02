package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.adapters.OrdersAdapter
import com.android.ecommerceadmin.databinding.FragmentOrdersBinding
import com.android.ecommerceadmin.util.VerticalItemDecoration


class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private val ordersAdapter by lazy {
        OrdersAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

    }

    fun setUpRecyclerView() {
        binding.orderRv.apply {
            adapter = ordersAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}