package com.atu.campus.services

import android.content.Context
import android.util.Base64
import com.atu.campus.data.StudentProfile
import com.atu.campus.ui.screens.FaceVerificationMode
import java.io.File
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

enum class StudentLookupStatus {
    Verified,
    NotFound,
    Ambiguous,
    BackendUnavailable
}

data class StudentLookupResult(
    val status: StudentLookupStatus,
    val profile: StudentProfile? = null,
    val cardNumber: String? = null,
    val message: String = ""
)

data class FaceAuthStartResult(
    val success: Boolean,
    val sessionId: String = "",
    val challengeType: String = "",
    val expiresInSeconds: Int = 0,
    val studentPreviewName: String = "",
    val studentPreviewGroup: String = "",
    val mode: FaceVerificationMode = FaceVerificationMode.Verify,
    val referencePhotoAvailable: Boolean = true,
    val message: String = ""
)

data class FaceAuthCompleteResult(
    val success: Boolean,
    val verified: Boolean,
    val profile: StudentProfile? = null,
    val matchScore: Double = 0.0,
    val livenessScore: Double = 0.0,
    val message: String = "",
    val failureReason: String = "",
    val recommendedAction: String = "",
    val confidenceBand: String = "",
    val captureQualityBand: String = "",
    val retryable: Boolean = true
)

class BackendStudentService(
    context: Context
) {
    private val backendConfigStore = BackendConfigStore(context)

    suspend fun lookupByCardScan(scan: CardNumberScanResult): StudentLookupResult =
        withContext(Dispatchers.IO) {
            if (scan.candidates.isEmpty()) {
                return@withContext StudentLookupResult(
                    status = StudentLookupStatus.NotFound,
                    message = "Vəsiqə nömrəsi oxunmadı. Kartı işıqlı mühitdə yenidən skan edin."
                )
            }

            val payload = JSONObject()
                .put("cardNumber", scan.cardNumber.orEmpty())
                .put("candidates", JSONArray(scan.candidates))

            var backendResponded = false
            for (baseUrl in backendConfigStore.resolveBaseUrls()) {
                val response = postJson(baseUrl, "/verify-card", payload) ?: continue
                backendResponded = true
                val status = response.optString("status")
                if (response.optBoolean("verified", false)) {
                    val student = response.optJSONObject("student") ?: continue
                    return@withContext StudentLookupResult(
                        status = StudentLookupStatus.Verified,
                        profile = student.toProfile(baseUrl),
                        cardNumber = student.optString("id")
                    )
                }
                if (status == "AMBIGUOUS") {
                    return@withContext StudentLookupResult(
                        status = StudentLookupStatus.Ambiguous,
                        cardNumber = response.optString("cardNumber"),
                        message = response.optString(
                            "message",
                            "Bu nömrə birdən çox tələbəyə aiddir. Məlumat administrator tərəfindən dəqiqləşdirilməlidir."
                        )
                    )
                }
            }

            if (backendResponded) {
                StudentLookupResult(
                    status = StudentLookupStatus.NotFound,
                    cardNumber = scan.cardNumber,
                    message = "Bu vəsiqə nömrəsi tələbə datasında tapılmadı."
                )
            } else {
                StudentLookupResult(
                    status = StudentLookupStatus.BackendUnavailable,
                    cardNumber = scan.cardNumber,
                    message = "Tələbə datası ilə əlaqə qurulmadı. Backend-i başladıb yenidən yoxlayın."
                )
            }
        }

    suspend fun lookupByCardNumber(cardNumber: String): StudentLookupResult {
        val normalized = cardNumber.filter(Char::isDigit).trimStart('0').ifBlank { "0" }
        return lookupByCardScan(
            CardNumberScanResult(
                cardNumber = normalized,
                candidates = listOf(normalized),
                rawText = normalized
            )
        )
    }

    suspend fun verifyScannedProfile(profile: StudentProfile): StudentProfile {
        return lookupByCardNumber(profile.id).profile ?: profile
    }

    suspend fun startFaceAuth(cardNumber: String, fin: String): FaceAuthStartResult =
        withContext(Dispatchers.IO) {
            val normalized = cardNumber.filter(Char::isDigit).trimStart('0').ifBlank { "0" }
            val payload = JSONObject()
                .put("studentNumber", normalized)
                .put("fin", fin.trim().uppercase())

            for (baseUrl in backendConfigStore.resolveBaseUrls()) {
                val response = postJson(baseUrl, "/auth/face/start", payload) ?: continue
                if (response.optBoolean("success", false)) {
                    val challenge = response.optJSONObject("challenge")
                    val preview = response.optJSONObject("studentPreview")
                    return@withContext FaceAuthStartResult(
                        success = true,
                        sessionId = response.optString("sessionId"),
                        challengeType = challenge?.optString("type").orEmpty(),
                        expiresInSeconds = challenge?.optInt("expiresInSeconds") ?: 0,
                        studentPreviewName = preview?.optString("fullName").orEmpty(),
                        studentPreviewGroup = preview?.optString("group").orEmpty(),
                        mode = if (response.optString("mode") == "ENROLL") {
                            FaceVerificationMode.Enroll
                        } else {
                            FaceVerificationMode.Verify
                        },
                        referencePhotoAvailable = response.optBoolean("referencePhotoAvailable", true)
                    )
                }
                return@withContext FaceAuthStartResult(
                    success = false,
                    message = response.optString("message", "Üz doğrulama sessiyası başladılmadı.")
                )
            }

            FaceAuthStartResult(
                success = false,
                message = "Face verification backend-ə qoşulmaq mümkün olmadı."
            )
        }

    suspend fun completeFaceAuth(
        sessionId: String,
        captures: List<String>,
        blinkDetected: Boolean,
        headTurnLeftDetected: Boolean
    ): FaceAuthCompleteResult = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("sessionId", sessionId)
            .put("captures", JSONArray(captures))
            .put(
                "challengeMeta",
                JSONObject()
                    .put("blinkDetected", blinkDetected)
                    .put("headTurnLeftDetected", headTurnLeftDetected)
            )

        for (baseUrl in backendConfigStore.resolveBaseUrls()) {
            val response = postJson(baseUrl, "/auth/face/complete", payload) ?: continue
            if (response.optBoolean("success", false) && response.optBoolean("verified", false)) {
                val student = response.optJSONObject("student")
                return@withContext FaceAuthCompleteResult(
                    success = true,
                    verified = true,
                    profile = student?.toProfile(baseUrl),
                    matchScore = response.optDouble("matchScore", 0.0),
                    livenessScore = response.optDouble("livenessScore", 0.0),
                    message = response.optString("message"),
                    confidenceBand = response.optString("confidenceBand"),
                    captureQualityBand = response.optString("captureQualityBand"),
                    retryable = response.optBoolean("retryable", false)
                )
            }
            return@withContext FaceAuthCompleteResult(
                success = false,
                verified = false,
                matchScore = response.optDouble("matchScore", 0.0),
                livenessScore = response.optDouble("livenessScore", 0.0),
                message = response.optString("message", "Üz doğrulaması uğursuz oldu."),
                failureReason = response.optString("failureReason"),
                recommendedAction = response.optString("recommendedAction"),
                confidenceBand = response.optString("confidenceBand"),
                captureQualityBand = response.optString("captureQualityBand"),
                retryable = response.optBoolean("retryable", true)
            )
        }

        FaceAuthCompleteResult(
            success = false,
            verified = false,
            message = "Face verification backend-ə qoşulmaq mümkün olmadı."
        )
    }

    fun imageFileToBase64(path: String): String {
        val file = File(path)
        if (!file.exists()) return ""
        val bytes = file.readBytes()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun JSONObject.toProfile(baseUrl: String): StudentProfile {
        val photoPath = optString("photoPath")
        return StudentProfile(
            surname = optString("surname"),
            name = optString("name"),
            fatherName = optString("fatherName"),
            id = optString("id"),
            fin = optString("fin"),
            identityCard = optString("identityCard"),
            faculty = optString("faculty"),
            department = optString("department"),
            specialty = optString("specialty"),
            group = optString("group"),
            photoUrl = when {
                photoPath.isBlank() -> ""
                photoPath.startsWith("http://") || photoPath.startsWith("https://") -> photoPath
                else -> "$baseUrl$photoPath"
            },
            course = optString("course"),
            studyForm = optString("studyForm"),
            educationLevel = optString("educationLevel"),
            status = optString("status")
        )
    }

    private fun postJson(baseUrl: String, path: String, payload: JSONObject): JSONObject? {
        val connection = (URL("$baseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 3500
            readTimeout = 5000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        return try {
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(payload.toString())
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
