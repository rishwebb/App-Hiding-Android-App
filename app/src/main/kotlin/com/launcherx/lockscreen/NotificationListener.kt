package com.launcherx.lockscreen

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = NotificationData(
            packageName = sbn.packageName,
            title = sbn.notification.extras.getString("android.title") ?: "",
            text = sbn.notification.extras.getString("android.text") ?: "",
            postTime = sbn.postTime,
            key = sbn.key
        )
        val current = _notifications.value.toMutableList()
        current.add(0, notification)
        // Keep max 5 notifications
        _notifications.value = current.take(5)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        _notifications.value = _notifications.value.filter { it.key != sbn.key }
    }

    companion object {
        private val _notifications = MutableStateFlow<List<NotificationData>>(emptyList())
        val notifications: StateFlow<List<NotificationData>> = _notifications.asStateFlow()
    }
}

data class NotificationData(
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: Long,
    val key: String
)
