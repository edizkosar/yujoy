package com.yujoy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yujoy.models.Notification
import com.yujoy.repositories.NotificationRepository
import com.yujoy.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NotificationState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val error: String? = null
)

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _notificationState = MutableStateFlow(NotificationState())
    val notificationState: StateFlow<NotificationState> = _notificationState.asStateFlow()

    init {
        listenToNotifications()
    }

    private fun listenToNotifications() {
        viewModelScope.launch {
            repository.getNotificationsFlow()
                .distinctUntilChanged()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _notificationState.value = NotificationState(
                                isLoading = false,
                                notifications = result.data
                            )
                        }
                        is Result.Error -> {
                            _notificationState.value = NotificationState(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        Result.Loading -> {
                            _notificationState.value = _notificationState.value.copy(isLoading = true)
                        }
                    }
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }
}