package com.atu.campus.services

import android.content.Context
import com.atu.campus.data.LocalProfileStorage
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FcmTokenSyncService(
    context: Context
) {
    private val appContext = context.applicationContext
    private val backendConfigStore = BackendConfigStore(appContext)
    private val localProfileStorage = LocalProfileStorage(appContext)
    private val preferences = appContext.getSharedPreferences("atu_fcm", Context.MODE_PRIVATE)

    suspend fun syncCurrentProfileToken(token: String) = withContext(Dispatchers.IO) {
        val profile = localProfileStorage.getProfile() ?: return@withContext
        registerToken(profile.id, token)
    }

    suspend fun registerToken(studentId: String, token: String): Boolean = withContext(Dispatchers.IO) {
        if (studentId.isBlank() || token.isBlank()) return@withContext false
        val payload = JSONObject()
            .put("studentId", studentId)
            .put("token", token)

        val success = post("/device-token/register", payload)
        if (success) {
            preferences.edit().putString(KEY_TOKEN, token).apply()
        }
        success
    }

    suspend fun unregisterStoredToken() = withContext(Dispatchers.IO) {
        val token = preferences.getString(KEY_TOKEN, "").orEmpty()
        if (token.isBlank()) return@withContext
        val payload = JSONObject().put("token", token)
        post("/device-token/unregister", payload)
        preferences.edit().remove(KEY_TOKEN).apply()
    }

    fun storePendingToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getStoredToken(): String = preferences.getString(KEY_TOKEN, "").orEmpty()

    private fun post(path: String, payload: JSONObject): Boolean {
        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val connection = (URL("$baseUrl$path").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 4000
                readTimeout = 8000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }
            try {
                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(payload.toString()) }
                if (connection.responseCode in 200..299) return true
            } catch (_: Exception) {
                // Try the next base URL.
            } finally {
                connection.disconnect()
            }
        }
        return false
    }

    companion object {
        private const val KEY_TOKEN = "device_token"
    }
}
