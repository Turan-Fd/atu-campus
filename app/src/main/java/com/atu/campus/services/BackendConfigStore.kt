package com.atu.campus.services

import android.content.Context

class BackendConfigStore(context: Context) {
    private val preferences =
        context.applicationContext.getSharedPreferences("atu_backend_config", Context.MODE_PRIVATE)

    fun getPreferredBaseUrl(): String =
        normalizeBaseUrl(preferences.getString(KEY_BASE_URL, "").orEmpty())

    fun savePreferredBaseUrl(value: String) {
        preferences.edit().putString(KEY_BASE_URL, normalizeBaseUrl(value)).apply()
    }

    fun resolveBaseUrls(): List<String> {
        val preferred = getPreferredBaseUrl()
        val defaults = listOf(
            "http://127.0.0.1:8080",
            "http://10.0.2.2:8080"
        )
        return buildList {
            if (preferred.isNotBlank()) add(preferred)
            addAll(defaults)
        }.distinct()
    }

    companion object {
        private const val KEY_BASE_URL = "preferred_base_url"
    }

    private fun normalizeBaseUrl(value: String): String {
        val trimmed = value.trim().trimEnd('/')
        if (trimmed.isBlank()) return ""
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "http://$trimmed"
        }
    }
}
