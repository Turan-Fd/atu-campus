package com.atu.campus.services

import android.content.Context
import android.net.Uri
import java.io.File

class CameraImageStore(private val context: Context) {
    private val scanDirectory: File
        get() = File(context.cacheDir, "scan_images").also { it.mkdirs() }

    fun createCardImageFile(): File {
        return File(scanDirectory, "student_card_${System.currentTimeMillis()}.jpg")
    }

    fun createFaceImageFile(): File {
        return File(scanDirectory, "face_capture_${System.currentTimeMillis()}.jpg")
    }

    fun uriFor(file: File): Uri = Uri.fromFile(file)

    fun clearCapturedImages() {
        scanDirectory.listFiles()?.forEach { file ->
            if (file.isFile) file.delete()
        }
    }
}
