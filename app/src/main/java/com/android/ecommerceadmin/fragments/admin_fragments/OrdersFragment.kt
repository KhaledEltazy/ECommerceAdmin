package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.OrdersAdapter
import com.android.ecommerceadmin.databinding.FragmentOrdersBinding
import com.android.ecommerceadmin.util.Constant.ORDER_DETAILS
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.util.VerticalItemDecoration
import com.android.ecommerceadmin.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private val ordersAdapter by lazy {
        OrdersAdapter()
    }
    private val ordersViewmodel by viewModels<OrdersViewModel>()

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

        ordersViewmodel.getAllOrders()

        collectGetOrders()

        ordersAdapter.onClicked = {
            //implementing Navigation
            val bundle = Bundle().apply {
                putParcelable(ORDER_DETAILS,it)
            }
            findNavController().navigate(R.id.action_ordersFragment_to_orderDetailsFragment,bundle)
        }

        //handling paging in recyclerView
        binding.orderRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isLoading = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dy > 0 && !isLoading) {
                    isLoading = true
                    ordersViewmodel.getAllOrders()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isLoading = false
                }
            }
        })


    }

    fun setUpRecyclerView() {
        binding.orderRv.apply {
            adapter = ordersAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    //collect getOrders data
    fun collectGetOrders(){
        lifecycleScope.launch {
            ordersViewmodel.getOrders.collect{
                when(it){
                    is Resource.Loading ->
                        binding.ordersProgressBar.visibility = View.VISIBLE
                    is Resource.Success ->{
                        binding.ordersProgressBar.visibility = View.GONE
                        ordersAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error ->{
                        binding.ordersProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}