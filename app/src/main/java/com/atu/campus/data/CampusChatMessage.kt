package com.atu.campus.data

data class CampusChatReaction(
    val emoji: String,
    val userId: String,
    val userName: String,
    val photoUrl: String
)

data class CampusChatMessage(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val senderPhotoUrl: String,
    val senderRole: String,
    val text: String,
    val mediaUrl: String,
    val mediaType: String,
    val attachmentName: String,
    val createdAt: Long,
    val editedAt: Long,
    val reactions: List<CampusChatReaction>
)
