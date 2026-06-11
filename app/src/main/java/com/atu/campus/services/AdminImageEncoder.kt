package com.atu.campus.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AdminSelectedImage(
    val uri: Uri,
    val fileName: String,
    val mimeType: String,
    val base64Data: String
)

object AdminImageEncoder {
    suspend fun encode(context: Context, uri: Uri): AdminSelectedImage? = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val bitmap = resolver.openInputStream(uri)?.use(BitmapFactory::decodeStream) ?: return@withContext null
        val output = ByteArrayOutputStream()
        val scaled = scaleBitmap(bitmap)
        scaled.compress(Bitmap.CompressFormat.JPEG, 86, output)
        if (scaled !== bitmap) scaled.recycle()

        AdminSelectedImage(
            uri = uri,
            fileName = "campus_${System.currentTimeMillis()}.jpg",
            mimeType = "image/jpeg",
            base64Data = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        )
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val maxSide = maxOf(bitmap.width, bitmap.height)
        if (maxSide <= 1440) return bitmap
        val scale = 1440f / maxSide.toFloat()
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt(),
            (bitmap.height * scale).toInt(),
            true
        )
    }
}
