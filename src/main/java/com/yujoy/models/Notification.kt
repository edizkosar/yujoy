package com.yujoy.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notification(
    @DocumentId
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "SYSTEM", // "EVENT_REMINDER", "CLUB_UPDATE", "SYSTEM", etc.
    val relatedEntityType: String? = null,
    val relatedEntityId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "title" to title,
            "message" to message,
            "type" to type,
            "relatedEntityType" to relatedEntityType,
            "relatedEntityId" to relatedEntityId,
            "isRead" to isRead,
            "createdAt" to (createdAt ?: Timestamp.now())
        )
    }
}