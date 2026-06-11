package com.atu.campus.services

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

data class CardNumberScanResult(
    val cardNumber: String?,
    val candidates: List<String>,
    val rawText: String
)

class OcrService(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun readCardNumber(imagePath: String?): CardNumberScanResult {
        val file = imagePath?.let(::File)
        if (file == null || !file.exists()) {
            return CardNumberScanResult(null, emptyList(), "")
        }

        val result = try {
            val image = InputImage.fromFilePath(context, Uri.fromFile(file))
            recognizer.process(image).await()
        } catch (_: Exception) {
            return CardNumberScanResult(null, emptyList(), "")
        }

        val lines = result.textBlocks
            .flatMap(Text.TextBlock::getLines)
            .map { it.text.trim() }
            .filter { it.isNotBlank() }

        val candidates = StudentCardNumberParser.findCandidates(lines)
        return CardNumberScanResult(
            cardNumber = candidates.firstOrNull(),
            candidates = candidates,
            rawText = lines.joinToString("\n")
        )
    }
}

private object StudentCardNumberParser {
    private val labelRegex = Regex(
        "(?i)(t\\u0259l\\u0259b\\u0259\\s*v\\u0259siq\\u0259si|" +
            "telebe\\s*vesiqesi|student\\s*(id|card)|" +
            "v\\u0259siq\\u0259\\s*(no|№|n\\u00F6mr\\u0259si)|kart\\s*(no|№))"
    )
    private val tokenRegex = Regex("""[A-Za-z0-9|]{4,10}""")

    fun findCandidates(lines: List<String>): List<String> {
        val prioritized = buildList {
            lines.forEachIndexed { index, line ->
                if (labelRegex.containsMatchIn(line)) {
                    add(line)
                    lines.getOrNull(index + 1)?.let(::add)
                }
            }
        }

        return (prioritized + lines)
            .flatMap { line -> tokenRegex.findAll(line).map { it.value }.toList() }
            .mapNotNull(::normalizeNumericToken)
            .distinct()
    }

    private fun normalizeNumericToken(token: String): String? {
        val normalized = token.uppercase()
            .replace('O', '0')
            .replace('Q', '0')
            .replace('D', '0')
            .replace('I', '1')
            .replace('L', '1')
            .replace('|', '1')
            .replace('Z', '2')
            .replace('S', '5')
            .replace('G', '6')
            .replace('B', '8')
            .filter(Char::isDigit)
            .trimStart('0')
            .ifBlank { "0" }

        return normalized.takeIf { it.length == 6 }
    }
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { exception -> continuation.resumeWithException(exception) }
    addOnCanceledListener { continuation.cancel() }
}
