package com.yujoy.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ClubCreationRequest(
    @DocumentId
    val requestId: String = "",
    val requestedBy: String = "",
    val requesterName: String = "",
    val clubName: String = "",
    val description: String = "",
    val justification: String = "",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val reviewedBy: String? = null,
    val reviewerName: String? = null,
    val reviewNotes: String? = null,
    val createdAt: Timestamp? = null,
    val reviewedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "requestedBy" to requestedBy,
            "requesterName" to requesterName,
            "clubName" to clubName,
            "description" to description,
            "justification" to justification,
            "status" to status,
            "reviewedBy" to reviewedBy,
            "reviewerName" to reviewerName,
            "reviewNotes" to reviewNotes,
            "createdAt" to (createdAt ?: Timestamp.now()),
            "reviewedAt" to reviewedAt
        )
    }
}