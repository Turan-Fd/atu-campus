package com.atu.campus.services

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AdminSelectedAttachment(
    val uri: Uri,
    val fileName: String,
    val mimeType: String,
    val base64Data: String
)

object AdminAttachmentEncoder {
    suspend fun encode(context: Context, uri: Uri): AdminSelectedAttachment? = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri).orEmpty().ifBlank { "application/octet-stream" }
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
        val fileName = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
            ?: "campus_${System.currentTimeMillis()}"

        AdminSelectedAttachment(
            uri = uri,
            fileName = fileName,
            mimeType = mimeType,
            base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
        )
    }
}
