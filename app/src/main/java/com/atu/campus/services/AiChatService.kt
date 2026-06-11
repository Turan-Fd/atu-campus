package com.atu.campus.services

import android.content.Context
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AiChatService(
    context: Context
) {
    private val backendConfigStore = BackendConfigStore(context)

    suspend fun sendMessage(message: String): String = withContext(Dispatchers.IO) {
        var lastError = "AI k\u00F6m\u0259k\u00E7i il\u0259 \u0259laq\u0259 qurulmad\u0131. Backend-in i\u015Fl\u0259diyin\u0259 \u0259min olun."
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val result = postMessage(baseUrl, message)
            if (result != null) return@withContext result
            lastError = "AI köməkçi backend-ə çata bilmədi. Eyni Wi‑Fi-də backend ünvanını tətbiq ayarlarında yoxlayın."
        }
        lastError
    }

    private fun postMessage(baseUrl: String, message: String): String? {
        val connection = (URL("$baseUrl/ai-chat").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 2500
            readTimeout = 30000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        return try {
            val payload = JSONObject().put("message", message)
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(payload.toString())
            }
            val stream = if (connection.responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            val json = if (body.isBlank()) JSONObject() else JSONObject(body)
            json.optString("answer").ifBlank {
                json.optString("error", "AI k\u00F6m\u0259k\u00E7i cavab qaytarmad\u0131.")
            }
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
