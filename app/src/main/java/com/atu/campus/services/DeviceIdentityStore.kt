package com.atu.campus.services

import android.content.Context
import android.provider.Settings
import java.util.UUID

class DeviceIdentityStore(
    context: Context
) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences("atu_device_identity", Context.MODE_PRIVATE)

    fun getDeviceId(): String {
        val stored = preferences.getString(KEY_DEVICE_ID, "").orEmpty()
        if (stored.isNotBlank()) return stored

        val androidId = runCatching {
            Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull().orEmpty()

        val resolved = when {
            androidId.isNotBlank() -> "android-$androidId"
            else -> "install-${UUID.randomUUID()}"
        }

        preferences.edit().putString(KEY_DEVICE_ID, resolved).apply()
        return resolved
    }

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
    }
}
