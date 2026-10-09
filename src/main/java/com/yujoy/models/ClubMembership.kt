package com.yujoy.models

import com.google.firebase.Timestamp

data class ClubMembership(
    val joinedAt: Timestamp? = null,
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "joinedAt" to (joinedAt ?: Timestamp.now()),
            "isActive" to isActive
        )
    }
}