package com.android.ecommerceadmin.fragments.admin_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.ecommerceadmin.databinding.FragmentPushNotificationBinding
import com.android.ecommerceadmin.viewmodel.NotificationStatus
import com.android.ecommerceadmin.viewmodel.PushNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PushNotificationFragment : Fragment() {
    private val pushNotificationViewModel by viewModels<PushNotificationViewModel>()
    private lateinit var binding: FragmentPushNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPushNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendNotification.setOnClickListener {
            val title = binding.notificationTitle.text.toString()
            val message = binding.notificationMessage.text.toString()

            if (title.isNotEmpty() && message.isNotEmpty()) {
                pushNotificationViewModel.sendPushNotification(title, message)
            }
        }

        lifecycleScope.launchWhenStarted {
            pushNotificationViewModel.sendStatus.collect { status ->
                when (status) {
                    is NotificationStatus.Loading -> {
                        binding.pushNotificationProgressBar.visibility = View.VISIBLE
                    }

                    is NotificationStatus.Success -> {
                        binding.pushNotificationProgressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Notification sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.notificationTitle.text.clear()
                        binding.notificationMessage.text.clear()
                    }

                    is NotificationStatus.Error -> {
                        binding.pushNotificationProgressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error: ${status.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is NotificationStatus.Idle -> Unit
                }
            }
        }
    }
}
