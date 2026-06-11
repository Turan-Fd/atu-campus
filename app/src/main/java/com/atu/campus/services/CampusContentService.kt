package com.atu.campus.services

import android.content.Context
import com.atu.campus.data.AtuNews
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class AdminLoginResult(
    val success: Boolean,
    val token: String = "",
    val message: String = ""
)

data class ContentPublishResult(
    val success: Boolean,
    val message: String
)

class CampusContentService(
    context: Context
) {
    private val backendConfigStore = BackendConfigStore(context)

    suspend fun fetchContent(since: Long = 0L): List<AtuNews> = withContext(Dispatchers.IO) {
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val suffix = if (since > 0L) "?since=$since" else ""
            val response = requestJson(baseUrl, "GET", "/campus-content$suffix") ?: continue
            val items = response.optJSONArray("items") ?: continue
            return@withContext buildList {
                for (index in 0 until items.length()) {
                    val item = items.optJSONObject(index) ?: continue
                    add(item.toNews(baseUrl))
                }
            }
        }
        emptyList()
    }

    suspend fun adminLogin(accessCode: String, password: String): AdminLoginResult =
        withContext(Dispatchers.IO) {
            val payload = JSONObject()
                .put("accessCode", accessCode)
                .put("password", password)
            for (baseUrl in backendConfigStore.resolveBaseUrls()) {
                val response = requestJson(baseUrl, "POST", "/admin/login", payload = payload) ?: continue
                if (response.optBoolean("authenticated")) {
                    return@withContext AdminLoginResult(true, response.optString("token"))
                }
                return@withContext AdminLoginResult(
                    false,
                    message = response.optString("message", "Admin giri\u015Fi u\u011Fursuz oldu.")
                )
            }
            AdminLoginResult(false, message = "Backend il\u0259 \u0259laq\u0259 qurulmad\u0131.")
        }

    suspend fun publishContent(
        token: String,
        type: String,
        title: String,
        summary: String,
        body: String,
        image: AdminSelectedImage?
    ): ContentPublishResult = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("type", type)
            .put("title", title)
            .put("summary", summary)
            .put("body", body)
            .put("imageUrl", "")
            .put("imageName", image?.fileName.orEmpty())
            .put("imageMimeType", image?.mimeType.orEmpty())
            .put("imageBase64", image?.base64Data.orEmpty())
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val response = requestJson(
                baseUrl,
                "POST",
                "/admin/content",
                payload = payload,
                token = token
            ) ?: continue
            return@withContext ContentPublishResult(
                response.optBoolean("success"),
                response.optString("message", if (response.optBoolean("success")) {
                    "M\u0259zmun yay\u0131mland\u0131."
                } else {
                    "M\u0259zmun yay\u0131mlanmad\u0131."
                })
            )
        }
        ContentPublishResult(false, "Backend il\u0259 \u0259laq\u0259 qurulmad\u0131.")
    }

    private fun JSONObject.toNews(baseUrl: String): AtuNews {
        val createdAt = optLong("createdAt")
        val formattedDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("az")).format(Date(createdAt))
        val rawImageUrl = optString("imageUrl")
        return AtuNews(
            id = optString("id"),
            type = optString("type", "NEWS"),
            title = optString("title"),
            date = formattedDate,
            summary = optString("summary"),
            url = "",
            imageUrl = if (rawImageUrl.startsWith("/")) "$baseUrl$rawImageUrl" else rawImageUrl,
            body = optString("body"),
            createdAt = createdAt
        )
    }

    private fun requestJson(
        baseUrl: String,
        method: String,
        path: String,
        payload: JSONObject? = null,
        token: String = ""
    ): JSONObject? {
        val connection = (URL("$baseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 3500
            readTimeout = 6000
            setRequestProperty("Accept", "application/json")
            if (token.isNotBlank()) setRequestProperty("Authorization", "Bearer $token")
            if (payload != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
        }
        return try {
            if (payload != null) {
                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(payload.toString()) }
            }
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (body.isBlank()) null else JSONObject(body)
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
