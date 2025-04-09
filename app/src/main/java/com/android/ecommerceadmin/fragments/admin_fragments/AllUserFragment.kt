package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerceadmin.adapters.AllUsersAdapter
import com.android.ecommerceadmin.databinding.FragmentAllUsersBinding
import com.android.ecommerceadmin.util.Resource
import com.android.ecommerceadmin.util.VerticalItemDecoration
import com.android.ecommerceadmin.viewmodel.AllUsersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllUserFragment : Fragment() {
    private lateinit var binding : FragmentAllUsersBinding
    private val allUsersAdapter by lazy {
        AllUsersAdapter()
    }
    private val allUsersViewmodel by viewModels<AllUsersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllUsersBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAllUserRecyclerView()

        allUsersAdapter.onClickedItem ={
            //implement navigation
        }

        collectAllUsers()

    }

    private fun setUpAllUserRecyclerView() {
        binding.allUsersRV.apply{
            adapter = allUsersAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    //collect AllUser
    private fun collectAllUsers(){
        lifecycleScope.launch {
            allUsersViewmodel.allUsers.collect{
                when(it){
                    is Resource.Loading ->
                        binding.allUserProgressBar.visibility = View.VISIBLE
                    is Resource.Success ->{
                        binding.allUserProgressBar.visibility = View.GONE
                        allUsersAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error ->{
                        binding.allUserProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }


}