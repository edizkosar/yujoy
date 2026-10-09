package com.yujoy.models

import com.google.firebase.Timestamp

data class EventRegistration(
    val registrationDate: Timestamp? = null,
    val attendanceStatus: String = "REGISTERED", // "REGISTERED", "ATTENDED", "CANCELLED", "NO_SHOW"
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "registrationDate" to (registrationDate ?: Timestamp.now()),
            "attendanceStatus" to attendanceStatus,
            "isActive" to isActive
        )
    }
}