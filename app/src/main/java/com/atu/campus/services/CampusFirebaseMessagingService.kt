package com.atu.campus.services

import com.atu.campus.data.CampusNotificationItem
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CampusFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val syncService = FcmTokenSyncService(applicationContext)
        syncService.storePendingToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val notificationService = CampusNotificationService(applicationContext)
        val title = data["title"].orEmpty().ifBlank { message.notification?.title.orEmpty() }
        val body = data["body"].orEmpty().ifBlank { message.notification?.body.orEmpty() }
        val item = CampusNotificationItem(
            id = data["id"].orEmpty().ifBlank { System.currentTimeMillis().toString() },
            type = data["type"].orEmpty().ifBlank { "DIRECT" },
            title = title,
            body = body,
            imageUrl = data["imageUrl"].orEmpty(),
            attachmentName = data["attachmentName"].orEmpty(),
            createdAt = data["createdAt"]?.toLongOrNull() ?: System.currentTimeMillis(),
            roomId = data["roomId"].orEmpty(),
            messageId = data["messageId"].orEmpty(),
            mediaType = data["mediaType"].orEmpty()
        )
        notificationService.show(item)
    }
}
