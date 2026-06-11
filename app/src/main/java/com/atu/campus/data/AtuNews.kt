package com.atu.campus.data

data class AtuNews(
    val id: String = "",
    val type: String = "NEWS",
    val title: String,
    val date: String,
    val summary: String,
    val url: String,
    val imageUrl: String,
    val body: String = "",
    val createdAt: Long = 0L
)
