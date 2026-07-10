package com.atu.campus.data

data class StudentProfile(
    val surname: String,
    val name: String,
    val fatherName: String,
    val id: String,
    val fin: String = "",
    val identityCard: String = "",
    val faculty: String,
    val department: String,
    val specialty: String,
    val group: String,
    val photoUrl: String = "",
    val course: String = "",
    val studyForm: String = "",
    val educationLevel: String = "",
    val status: String = "",
    val authSessionToken: String = "",
    val sessionDeviceId: String = ""
) {
    val fullName: String
        get() = "$name $surname".trim()
}
