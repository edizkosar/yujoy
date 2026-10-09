package com.yujoy.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Event(
    @DocumentId
    val eventId: String = "",
    val clubId: String = "",
    val clubName: String = "", // Denormalized
    val clubLogoUrl: String? = null, // Denormalized
    val title: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val location: String = "",
    val eventDate: Timestamp? = null,
    val registrationStartDate: Timestamp? = null,
    val registrationEndDate: Timestamp? = null,
    val capacity: Int = 0,
    val currentRegistrations: Int = 0,
    val availableSpots: Int = 0,
    val isActive: Boolean = true,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "clubId" to clubId,
            "clubName" to clubName,
            "clubLogoUrl" to clubLogoUrl,
            "title" to title,
            "description" to description,
            "coverImageUrl" to coverImageUrl,
            "location" to location,
            "eventDate" to eventDate,
            "registrationStartDate" to registrationStartDate,
            "registrationEndDate" to registrationEndDate,
            "capacity" to capacity,
            "currentRegistrations" to currentRegistrations,
            "availableSpots" to availableSpots,
            "isActive" to isActive,
            "createdBy" to createdBy,
            "createdAt" to (createdAt ?: Timestamp.now()),
            "updatedAt" to Timestamp.now()
        )
    }

    fun canRegister(): Boolean {
        val now = Timestamp.now()
        return isActive &&
                currentRegistrations < capacity &&
                registrationStartDate != null &&
                registrationEndDate != null &&
                now.seconds >= registrationStartDate.seconds &&
                now.seconds <= registrationEndDate.seconds
    }

    fun getRegistrationStatus(): RegistrationStatus {
        val now = Timestamp.now()
        return when {
            !isActive -> RegistrationStatus.INACTIVE
            registrationStartDate == null || registrationEndDate == null -> RegistrationStatus.INVALID
            now.seconds < registrationStartDate.seconds -> RegistrationStatus.NOT_STARTED
            now.seconds > registrationEndDate.seconds -> RegistrationStatus.CLOSED
            currentRegistrations >= capacity -> RegistrationStatus.FULL
            else -> RegistrationStatus.AVAILABLE
        }
    }
}

enum class RegistrationStatus {
    AVAILABLE, NOT_STARTED, CLOSED, FULL, INVALID, INACTIVE
}