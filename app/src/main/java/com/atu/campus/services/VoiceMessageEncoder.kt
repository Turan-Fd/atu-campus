package com.atu.campus.services

import android.net.Uri
import android.util.Base64
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object VoiceMessageEncoder {
    suspend fun encode(file: File): AdminSelectedAttachment? = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext null
        val bytes = file.readBytes()
        AdminSelectedAttachment(
            uri = Uri.fromFile(file),
            fileName = file.name.ifBlank { "voice_${System.currentTimeMillis()}.m4a" },
            mimeType = "audio/mp4",
            base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
        )
    }
}
