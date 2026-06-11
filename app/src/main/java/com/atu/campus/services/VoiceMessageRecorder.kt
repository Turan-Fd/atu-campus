package com.atu.campus.services

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class VoiceMessageRecorder(
    private val context: Context
) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun start(): Boolean {
        return try {
            val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
            val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(96_000)
                setAudioSamplingRate(44_100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            recorder = mediaRecorder
            outputFile = file
            true
        } catch (_: Exception) {
            stopAndDelete()
            false
        }
    }

    fun stop(): File? {
        val currentRecorder = recorder ?: return null
        return try {
            currentRecorder.stop()
            currentRecorder.reset()
            currentRecorder.release()
            recorder = null
            outputFile
        } catch (_: Exception) {
            stopAndDelete()
            null
        }
    }

    fun stopAndDelete() {
        try {
            recorder?.stop()
        } catch (_: Exception) {
        }
        try {
            recorder?.reset()
            recorder?.release()
        } catch (_: Exception) {
        }
        recorder = null
        outputFile?.delete()
        outputFile = null
    }
}
