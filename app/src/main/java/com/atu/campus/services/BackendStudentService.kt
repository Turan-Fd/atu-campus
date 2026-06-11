package com.atu.campus.services

import android.content.Context
import com.atu.campus.data.StudentProfile
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

class BackendStudentService(
    context: Context
) {
    private val backendConfigStore = BackendConfigStore(context)

    suspend fun lookupByCardScan(scan: CardNumberScanResult): StudentLookupResult =
        withContext(Dispatchers.IO) {
            if (scan.candidates.isEmpty()) {
                return@withContext StudentLookupResult(
                    status = StudentLookupStatus.NotFound,
                    message = "V\u0259siq\u0259 n\u00F6mr\u0259si oxunmad\u0131. Kart\u0131 i\u015F\u0131ql\u0131 m\u00FChitd\u0259 yenid\u0259n skan edin."
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
                            "Bu n\u00F6mr\u0259 bird\u0259n \u00E7ox t\u0259l\u0259b\u0259y\u0259 aiddir. M\u0259lumat administrator t\u0259r\u0259find\u0259n d\u0259qiql\u0259\u015Fdirilm\u0259lidir."
                        )
                    )
                }
            }

            if (backendResponded) {
                StudentLookupResult(
                    status = StudentLookupStatus.NotFound,
                    cardNumber = scan.cardNumber,
                    message = "Bu v\u0259siq\u0259 n\u00F6mr\u0259si t\u0259l\u0259b\u0259 datas\u0131nda tap\u0131lmad\u0131."
                )
            } else {
                StudentLookupResult(
                    status = StudentLookupStatus.BackendUnavailable,
                    cardNumber = scan.cardNumber,
                    message = "T\u0259l\u0259b\u0259 datas\u0131 il\u0259 \u0259laq\u0259 qurulmad\u0131. Backend-i ba\u015Flad\u0131b yenid\u0259n yoxlay\u0131n."
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

    private fun JSONObject.toProfile(baseUrl: String): StudentProfile {
        val photoPath = optString("photoPath")
        return StudentProfile(
            surname = optString("surname"),
            name = optString("name"),
            fatherName = optString("fatherName"),
            id = optString("id"),
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
