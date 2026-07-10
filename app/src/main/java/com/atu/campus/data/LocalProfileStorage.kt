package com.atu.campus.data

import android.content.Context

class LocalProfileStorage(context: Context) {
    private val preferences = context.getSharedPreferences("atu_profile", Context.MODE_PRIVATE)

    fun getProfile(): StudentProfile? {
        val id = preferences.getString(KEY_ID, null) ?: return null
        return StudentProfile(
            surname = preferences.getString(KEY_SURNAME, "").orEmpty(),
            name = preferences.getString(KEY_NAME, "").orEmpty(),
            fatherName = preferences.getString(KEY_FATHER_NAME, "").orEmpty(),
            id = id,
            fin = preferences.getString(KEY_FIN, "").orEmpty(),
            identityCard = preferences.getString(KEY_IDENTITY_CARD, "").orEmpty(),
            faculty = preferences.getString(KEY_FACULTY, "").orEmpty(),
            department = preferences.getString(KEY_DEPARTMENT, "").orEmpty(),
            specialty = preferences.getString(KEY_SPECIALTY, "").orEmpty(),
            group = preferences.getString(KEY_GROUP, "").orEmpty(),
            photoUrl = preferences.getString(KEY_PHOTO_URL, "").orEmpty(),
            course = preferences.getString(KEY_COURSE, "").orEmpty(),
            studyForm = preferences.getString(KEY_STUDY_FORM, "").orEmpty(),
            educationLevel = preferences.getString(KEY_EDUCATION_LEVEL, "").orEmpty(),
            status = preferences.getString(KEY_STATUS, "").orEmpty(),
            authSessionToken = preferences.getString(KEY_AUTH_SESSION_TOKEN, "").orEmpty(),
            sessionDeviceId = preferences.getString(KEY_SESSION_DEVICE_ID, "").orEmpty()
        )
    }

    fun saveProfile(profile: StudentProfile) {
        preferences.edit()
            .putString(KEY_SURNAME, profile.surname)
            .putString(KEY_NAME, profile.name)
            .putString(KEY_FATHER_NAME, profile.fatherName)
            .putString(KEY_ID, profile.id)
            .putString(KEY_FIN, profile.fin)
            .putString(KEY_IDENTITY_CARD, profile.identityCard)
            .putString(KEY_FACULTY, profile.faculty)
            .putString(KEY_DEPARTMENT, profile.department)
            .putString(KEY_SPECIALTY, profile.specialty)
            .putString(KEY_GROUP, profile.group)
            .putString(KEY_PHOTO_URL, profile.photoUrl)
            .putString(KEY_COURSE, profile.course)
            .putString(KEY_STUDY_FORM, profile.studyForm)
            .putString(KEY_EDUCATION_LEVEL, profile.educationLevel)
            .putString(KEY_STATUS, profile.status)
            .putString(KEY_AUTH_SESSION_TOKEN, profile.authSessionToken)
            .putString(KEY_SESSION_DEVICE_ID, profile.sessionDeviceId)
            .apply()
    }

    fun clearProfile() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val KEY_SURNAME = "surname"
        const val KEY_NAME = "name"
        const val KEY_FATHER_NAME = "father_name"
        const val KEY_ID = "id"
        const val KEY_FIN = "fin"
        const val KEY_IDENTITY_CARD = "identity_card"
        const val KEY_FACULTY = "faculty"
        const val KEY_DEPARTMENT = "department"
        const val KEY_SPECIALTY = "specialty"
        const val KEY_GROUP = "group"
        const val KEY_PHOTO_URL = "photo_url"
        const val KEY_COURSE = "course"
        const val KEY_STUDY_FORM = "study_form"
        const val KEY_EDUCATION_LEVEL = "education_level"
        const val KEY_STATUS = "status"
        const val KEY_AUTH_SESSION_TOKEN = "auth_session_token"
        const val KEY_SESSION_DEVICE_ID = "session_device_id"
    }
}
