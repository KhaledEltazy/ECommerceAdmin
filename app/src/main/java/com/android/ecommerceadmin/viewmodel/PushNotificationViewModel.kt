package com.android.ecommerceadmin.viewmodel

import PushNotificationRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushNotificationViewModel @Inject constructor(
    private val pushNotificationRepository: PushNotificationRepository
) : ViewModel() {

    private val _sendStatus = MutableStateFlow<NotificationStatus>(NotificationStatus.Idle)
    val sendStatus = _sendStatus.asStateFlow()

    fun sendPushNotification(title: String, message: String) {
        viewModelScope.launch {
            _sendStatus.emit(NotificationStatus.Loading)
            try {
                // Send notification using repository
                pushNotificationRepository.sendNotification(title, message)
                _sendStatus.emit(NotificationStatus.Success)
            } catch (e: Exception) {
                _sendStatus.emit(NotificationStatus.Error("Failed to send: ${e.localizedMessage}"))
            }
        }
    }
}

sealed class NotificationStatus {
    object Idle : NotificationStatus()
    object Loading : NotificationStatus()
    object Success : NotificationStatus()
    data class Error(val message: String) : NotificationStatus()
}