package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.ecommerceadmin.R
import com.android.ecommerceadmin.adapters.GridViewAdapter
import com.android.ecommerceadmin.databinding.FragmentHomeAdminBinding
import com.android.ecommerceadmin.util.GridItemsList
import com.android.ecommerceadmin.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeAdminBinding

    private lateinit var itemList: GridItemsList

    private lateinit var gridAdapter: GridViewAdapter

    private val settingsViewModel by viewModels<SettingsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = GridItemsList(requireContext())
        gridAdapter = GridViewAdapter(requireContext(), itemList.getGridItemList)
        binding.gvAdmin.adapter = gridAdapter

        //handle navigation of each icon
        binding.gvAdmin.setOnItemClickListener { _, _, position, _ ->
            val item = itemList.getGridItemList[position]
            when (item.title) {
                getString(R.string.add_product) ->
                    findNavController().navigate(R.id.action_homeAdminFragment_to_addProductFragment)

                getString(R.string.all_products) ->
                    findNavController().navigate(R.id.action_homeAdminFragment_to_categoriesFragment)

                getString(R.string.orders) ->
                    findNavController().navigate(R.id.action_homeAdminFragment_to_ordersFragment)

                getString(R.string.all_users) ->
                    findNavController().navigate(R.id.action_homeAdminFragment_to_allUserFragment)

                getString(R.string.reports) ->
                    Toast.makeText(
                        requireContext(),
                        "you Selected ${item.title}",
                        Toast.LENGTH_SHORT
                    ).show()

                getString(R.string.admin_settings) ->
                    findNavController().navigate(R.id.action_homeAdminFragment_to_adminSettingFragment)
            }
        }

        binding.switchMode.isChecked = settingsViewModel.isDarkMode.value
        binding.switchLanguage.isChecked = settingsViewModel.isArabic.value

        binding.switchMode.setOnCheckedChangeListener { _, isChecked ->
            binding.switchMode.setOnCheckedChangeListener { _, isChecked ->
                settingsViewModel.toggleDarkMode(isChecked)
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleLanguage(isChecked)
            val locale = Locale(if (isChecked) "ar" else "en")
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            requireActivity().resources.updateConfiguration(config, resources.displayMetrics)

            val navController = findNavController()
            navController.popBackStack()
            navController.navigate(R.id.homeAdminFragment)
        }


    }
}