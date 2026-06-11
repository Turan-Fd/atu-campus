package com.atu.campus.data

data class CampusChatRoom(
    val id: String,
    val title: String,
    val subtitle: String,
    val kind: String,
    val readOnly: Boolean,
    val group: String,
    val lastMessagePreview: String,
    val lastMessageTime: Long
)
