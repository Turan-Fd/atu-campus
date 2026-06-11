package com.atu.campus.data

data class CampusNotificationItem(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val imageUrl: String,
    val attachmentName: String,
    val createdAt: Long,
    val roomId: String = "",
    val messageId: String = "",
    val mediaType: String = ""
)
