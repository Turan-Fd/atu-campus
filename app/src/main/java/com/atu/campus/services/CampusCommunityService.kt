package com.atu.campus.services

import android.content.Context
import com.atu.campus.data.CampusChatMessage
import com.atu.campus.data.CampusChatReaction
import com.atu.campus.data.CampusChatRoom
import com.atu.campus.data.CampusNotificationItem
import com.atu.campus.data.StudentDirectoryEntry
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class CommunityActionResult(
    val success: Boolean,
    val message: String
)

class CampusCommunityService(
    context: Context
) {
    private val backendConfigStore = BackendConfigStore(context)

    suspend fun fetchNotifications(studentId: String): List<CampusNotificationItem> = withContext(Dispatchers.IO) {
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val response = requestJson(baseUrl, "GET", "/notifications?studentId=$studentId") ?: continue
            val items = response.optJSONArray("items") ?: continue
            return@withContext buildList {
                for (index in 0 until items.length()) {
                    val item = items.optJSONObject(index) ?: continue
                    add(
                        CampusNotificationItem(
                            id = item.optString("id"),
                            type = item.optString("type"),
                            title = item.optString("title"),
                            body = item.optString("body"),
                            imageUrl = mapAsset(baseUrl, item.optString("imageUrl")),
                            attachmentName = item.optString("attachmentName"),
                            createdAt = item.optLong("createdAt"),
                            roomId = item.optString("roomId"),
                            messageId = item.optString("messageId"),
                            mediaType = item.optString("mediaType")
                        )
                    )
                }
            }
        }
        emptyList()
    }

    suspend fun searchStudents(query: String): List<StudentDirectoryEntry> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            val response = requestJson(baseUrl, "GET", "/students?query=$encoded") ?: continue
            val matches = response.optJSONArray("matches") ?: continue
            return@withContext buildList {
                for (index in 0 until matches.length()) {
                    val item = matches.optJSONObject(index) ?: continue
                    add(
                        StudentDirectoryEntry(
                            id = item.optString("id"),
                            fullName = listOf(item.optString("name"), item.optString("surname")).joinToString(" ").trim(),
                            group = item.optString("group"),
                            specialty = item.optString("specialty"),
                            photoUrl = mapAsset(baseUrl, item.optString("photoPath"))
                        )
                    )
                }
            }
        }
        emptyList()
    }

    suspend fun fetchRooms(studentId: String): List<CampusChatRoom> = withContext(Dispatchers.IO) {
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val response = requestJson(baseUrl, "GET", "/chat/rooms?studentId=$studentId") ?: continue
            val rooms = response.optJSONArray("rooms") ?: continue
            return@withContext buildList {
                for (index in 0 until rooms.length()) {
                    val item = rooms.optJSONObject(index) ?: continue
                    val lastMessage = item.optJSONObject("lastMessage")
                    add(
                        CampusChatRoom(
                            id = item.optString("id"),
                            title = item.optString("title"),
                            subtitle = item.optString("subtitle"),
                            kind = item.optString("kind"),
                            readOnly = item.optBoolean("readOnly"),
                            group = item.optString("group"),
                            lastMessagePreview = lastMessage?.optString("text").orEmpty().ifBlank {
                                when (item.optString("kind")) {
                                    "OFFICIAL" -> "RÉsmi universitet elanlarÄ± burada gÃ¶rÃ¼nÉcÉk"
                                    else -> "SÃ¶hbÉti baÅlat"
                                }
                            },
                            lastMessageTime = lastMessage?.optLong("createdAt") ?: 0L
                        )
                    )
                }
            }
        }
        emptyList()
    }

    suspend fun fetchMessages(roomId: String, since: Long = 0L): List<CampusChatMessage> = withContext(Dispatchers.IO) {
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val response = requestJson(baseUrl, "GET", "/chat/messages?roomId=$roomId&since=$since") ?: continue
            val messages = response.optJSONArray("messages") ?: continue
            return@withContext buildList {
                for (index in 0 until messages.length()) {
                    val item = messages.optJSONObject(index) ?: continue
                    val reactions = item.optJSONArray("reactions")
                    add(
                        CampusChatMessage(
                            id = item.optString("id"),
                            roomId = item.optString("roomId"),
                            senderId = item.optString("senderId"),
                            senderName = item.optString("senderName"),
                            senderPhotoUrl = mapAsset(baseUrl, item.optString("senderPhotoPath")),
                            senderRole = item.optString("senderRole"),
                            text = item.optString("text"),
                            mediaUrl = mapAsset(baseUrl, item.optString("mediaUrl")),
                            mediaType = item.optString("mediaType"),
                            attachmentName = item.optString("attachmentName"),
                            createdAt = item.optLong("createdAt"),
                            editedAt = item.optLong("editedAt"),
                            reactions = buildList {
                                if (reactions != null) {
                                    for (reactionIndex in 0 until reactions.length()) {
                                        val reaction = reactions.optJSONObject(reactionIndex) ?: continue
                                        add(
                                            CampusChatReaction(
                                                emoji = reaction.optString("emoji"),
                                                userId = reaction.optString("userId"),
                                                userName = reaction.optString("userName"),
                                                photoUrl = mapAsset(baseUrl, reaction.optString("photoPath"))
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    )
                }
            }
        }
        emptyList()
    }

    suspend fun sendMessage(
        roomId: String,
        studentId: String,
        text: String,
        image: AdminSelectedImage? = null,
        attachment: AdminSelectedAttachment? = null,
        adminToken: String = ""
    ): CommunityActionResult = withContext(Dispatchers.IO) {
        val mediaName = attachment?.fileName ?: image?.fileName.orEmpty()
        val mediaMimeType = attachment?.mimeType ?: image?.mimeType.orEmpty()
        val mediaBase64 = attachment?.base64Data ?: image?.base64Data.orEmpty()
        val payload = JSONObject()
            .put("roomId", roomId)
            .put("studentId", studentId)
            .put("text", text)
            .put("imageName", mediaName)
            .put("imageMimeType", mediaMimeType)
            .put("imageBase64", mediaBase64)
        postAction("/chat/message", payload, adminToken, "Mesaj gÃ¶ndÉrildi.", "Mesaj gÃ¶ndÉrilmÉdi.")
    }


    suspend fun toggleReaction(messageId: String, studentId: String, emoji: String): CommunityActionResult =
        withContext(Dispatchers.IO) {
            val payload = JSONObject()
                .put("messageId", messageId)
                .put("studentId", studentId)
                .put("emoji", emoji)
            postAction("/chat/reaction", payload, "", "Reaksiya yenilÉndi.", "Reaksiya yenilÉnmÉdi.")
        }

    suspend fun updateMessage(
        messageId: String,
        studentId: String,
        action: String,
        text: String = "",
        adminToken: String = ""
    ): CommunityActionResult = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("messageId", messageId)
            .put("studentId", studentId)
            .put("action", action)
            .put("text", text)
        postAction("/chat/message-action", payload, adminToken, "Mesaj yenilÉndi.", "Mesaj yenilÉnmÉdi.")
    }

    suspend fun sendDirectNotification(
        token: String,
        studentIds: List<String>,
        title: String,
        body: String,
        type: String,
        attachment: AdminSelectedAttachment?
    ): CommunityActionResult = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("studentIds", org.json.JSONArray(studentIds))
            .put("title", title)
            .put("body", body)
            .put("type", type)
            .put("attachmentName", attachment?.fileName.orEmpty())
            .put("attachmentMimeType", attachment?.mimeType.orEmpty())
            .put("attachmentBase64", attachment?.base64Data.orEmpty())
        postAction(
            "/admin/direct-notification",
            payload,
            token,
            "BildiriÅ gÃ¶ndÉrildi.",
            "BildiriÅ gÃ¶ndÉrilmÉdi."
        )
    }

    private fun mapAsset(baseUrl: String, path: String): String =
        if (path.startsWith("/")) "$baseUrl$path" else path

    private fun postAction(
        path: String,
        payload: JSONObject,
        token: String,
        successFallback: String,
        errorFallback: String
    ): CommunityActionResult {
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val response = requestJson(baseUrl, "POST", path, payload, token) ?: continue
            return CommunityActionResult(
                success = response.optBoolean("success", true),
                message = response.opt("message")?.takeIf { it is String } as? String
                    ?: if (response.optBoolean("success", true)) successFallback else errorFallback
            )
        }
        return CommunityActionResult(false, "Backend ilÉ ÉlaqÉ qurulmadÄ±.")
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
            readTimeout = 7000
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
            val stream = if (connection.responseCode in 200..499) {
                if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            } else {
                connection.errorStream
            }
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (body.isBlank()) null else JSONObject(body)
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
