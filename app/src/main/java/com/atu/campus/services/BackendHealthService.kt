package com.atu.campus.services

import android.content.Context
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackendHealthService(context: Context) {
    private val backendConfigStore = BackendConfigStore(context)

    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val urls = backendConfigStore.resolveBaseUrls()
        if (urls.isEmpty()) {
            return@withContext false to "Backend ünvanı daxil edilməyib."
        }
        for (baseUrl in urls) {
            val ok = ping("$baseUrl/health")
            if (ok) return@withContext true to "Bağlantı aktivdir: $baseUrl"
        }
        false to "Backend-ə qoşulmaq olmadı. Wi‑Fi, IP və firewall-u yoxlayın."
    }

    private fun ping(url: String): Boolean {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 3000
            readTimeout = 3000
        }
        return try {
            connection.responseCode in 200..299
        } catch (_: Exception) {
            false
        } finally {
            connection.disconnect()
        }
    }
}
